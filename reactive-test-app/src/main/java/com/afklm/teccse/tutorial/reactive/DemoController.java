package com.afklm.teccse.tutorial.reactive;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.afklm.soa.stubs.w000479.v1.ProvideUserRightsAccessV10;
import com.afklm.soa.stubs.w000479.v1.rights.ProvideUserRightsAccessRQ;
import com.afklm.soa.stubs.w000479.v1.rights.ProvideUserRightsAccessRS;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class DemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    private static String baseUrl = "http://localhost:8082?delay=2";
    private static RestTemplate restTemplate = new RestTemplate();
    private static WebClient webClient = WebClient.builder().baseUrl(baseUrl).filter(logRequest()).build();
    
    @Autowired
    private ProvideUserRightsAccessV10 soaProvider;

    public DemoController() {
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
    }

    @GetMapping("/backend/people")
    public List<Person> getBackendPeople() {
        Instant start = Instant.now();
        List<Integer> ids = restTemplate.getForObject("/person", List.class);
        List<Person> result = ids.stream().map(id -> restTemplate.getForObject("/person/{id}", Person.class, id))
                .toList();
        logTime(start);
        return result;
    }

    @GetMapping(value = "/backend/people/reactive")
    public Flux<Person> getReactive() {
        Instant start = Instant.now();
        
        return webClient.get().uri("/person").retrieve().bodyToFlux(Integer.class)
            .flatMap(id -> 
                Mono.zip(
                    webClient.get().uri("/person/{id}", id).retrieve().bodyToMono(Person.class)
                        .flatMap(person -> getSoa(person.name()).flatMap(soaresponse -> Mono.just(new Person(person.id(), soaresponse.getFirstName())))),
                        webClient.get().uri("/person/{id}/domain", id).retrieve().bodyToMono(String.class),
                    (person, domain) -> new Person(person.id(), person.name(), domain)
                )
            )
            .doFinally(type -> logTime(start))
            ;
    }

    @GetMapping(value = "/soa")
    public Mono<ProvideUserRightsAccessRS> getSoa(String userId) {
        ProvideUserRightsAccessRQ request = new ProvideUserRightsAccessRQ();
        request.setUserId(userId);
        
        return Mono.<ProvideUserRightsAccessRS>create(sink -> soaProvider.provideUserRightsAccessAsync(request, response -> {
            try {
                sink.success(response.get());
            } catch (InterruptedException | ExecutionException e) {
                sink.error(e);
            }
        }));
    }



    static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            LOGGER.info(clientRequest.url().toString());
            return Mono.just(clientRequest);
        });
    }

    private void logTime(Instant start) {
        LOGGER.info(">>> Duration: " + Duration.between(start, Instant.now()).toMillis() + "ms");
    }
}

record Person(Integer id, String name, String domain) {
    Person(Integer id, String name) {
        this(id, name, null);
    }
}