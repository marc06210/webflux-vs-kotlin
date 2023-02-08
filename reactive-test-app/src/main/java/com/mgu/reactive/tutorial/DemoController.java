package com.mgu.reactive.tutorial;

import com.mgu.reactive.tutorial.entity.User;
import com.mgu.reactive.tutorial.entity.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
public class DemoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    private static final String baseUrl = "http://localhost:8082?delay=2";
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final WebClient webClient = WebClient
            .builder()
            .baseUrl(baseUrl)
            .filter(logRequest())
            .filter(logResponse())
            .build();

    private final UserRepository userRepository;

    static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            LOGGER.info(clientRequest.url().toString());
            return Mono.just(clientRequest);
        });
    }

    static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(serverResponse -> {
            LOGGER.info("MGU >>> response");
            return Mono.just(serverResponse);
        });
    }

    public DemoController(UserRepository userRepository) {
        this.userRepository = userRepository;
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
    }
    
    @GetMapping("/people")
    public List<Person> getBackendPeople() {
        var start = Instant.now();
        @SuppressWarnings("unchecked")
        List<Integer> ids = restTemplate.getForObject("/persons", List.class);
        List<Person> result = ids
                .parallelStream()
                .map(id -> restTemplate.getForObject("/persons/{id}", Person.class, id))
                .toList();
        logTime(start);
        return result;
    }

    @GetMapping(value = "/people/ids")
    public Flux<Long> getBEIds() {
        var start = Instant.now();
        return webClient.get().uri("/persons").retrieve().bodyToFlux(Long.class)
                .doFinally(signal -> logTime(start))
                ;
    }

    @GetMapping(value="/people/{id}")
    public User getPerson(@PathVariable Long id) {
        return userRepository.findById(id).orElse(new User(-1L, "N/A"));
    }

    @GetMapping(value = "/people/reactive")
    public Flux<Person> getBEReactive() {
        var start = Instant.now();
        return webClient.get().uri("/persons").retrieve().bodyToFlux(Long.class)
            .flatMap(id ->
                Mono.zip(
                        Mono.just(userRepository.findById(id)).map(Optional::orElseThrow).map(user -> new Person(id, user.getName())),
                        webClient.get().uri("/persons/{id}/domain", id).retrieve().bodyToMono(String.class),
                        (person, domain) -> new Person(id, person.name(), domain)
                )
            )
            .doFinally(signal -> logTime(start))
            ;
    }
    
    private void logTime(Instant start) {
        LOGGER.info(">>> Duration: " + Duration.between(start, Instant.now()).toMillis() + "ms");
    }
}

record Person(Long id, String name, String domain) {
    Person(Long id, String name) {
        this(id, name, null);
    }
}