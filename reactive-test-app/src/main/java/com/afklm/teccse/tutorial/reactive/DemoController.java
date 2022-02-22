package com.afklm.teccse.tutorial.reactive;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private ProvideUserRightsAccessV10 bean;

    static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            LOGGER.info(clientRequest.url().toString());
            return Mono.just(clientRequest);
        });
    }

    
    public DemoController() {
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
                .map(person -> new Person(person.id(), person.name(), restTemplate.getForObject("/persons/{id}/domain", String.class, person.id())))
                .toList();
//                new ArrayList<>();
//        for (Integer id : ids) {
//            Person person = restTemplate.getForObject("/persons/{id}", Person.class, id);
//            String domain = restTemplate.getForObject("/persons/{id}/domain", String.class, id);
//            result.add(new Person(person.id(), person.name(), domain));
//        }
        logTime(start);
        return result;
    }
    
    @GetMapping(value = "/people/reactive")
    public Flux<Person> getBEReactive() {
        var start = Instant.now();
        return webClient.get().uri("/persons").retrieve().bodyToFlux(Long.class)
            .flatMap(id -> 
                Mono.zip(
                        webClient.get().uri("/persons/{id}", id).retrieve().bodyToMono(Person.class)
                            .flatMap(person -> getHblUser(person.name()).map(hblUser -> new Person(person.id(), hblUser.getFirstName()))),
                        webClient.get().uri("/persons/{id}/domain", id).retrieve().bodyToMono(String.class),
                        (person, domain) -> new Person(id, person.name(), domain)
                )
            )
            .doFinally(signal -> logTime(start))
            ;
    }
    
    @GetMapping(value = "/soa")
    public Mono<ProvideUserRightsAccessRS> getHblUser(@RequestParam String userId) {
        ProvideUserRightsAccessRQ request = new ProvideUserRightsAccessRQ();
        request.setUserId(userId);
        return Mono.create(sink -> bean.provideUserRightsAccessAsync(request, soareponse -> {
            try {
                sink.success(soareponse.get());
            } catch (InterruptedException | ExecutionException e) {
                sink.error(e);
            }
        }));
//        try {
//            return bean.provideUserRightsAccessAsync(request).get();
//        } catch (InterruptedException | ExecutionException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//        }
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