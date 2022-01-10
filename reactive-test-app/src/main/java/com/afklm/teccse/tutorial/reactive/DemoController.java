package com.afklm.teccse.tutorial.reactive;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    
    @Autowired
    private ProvideUserRightsAccessV10 provider;

    private static String baseUrl = "http://localhost:8082";
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);
    private static RestTemplate restTemplate = new RestTemplate();
    private static WebClient webClient = WebClient.builder().baseUrl(baseUrl).filter(logRequest()).build();

    public DemoController() {
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
    }

    @GetMapping("/backend/people")
    public List<Person> getBackendPeople() {
        List<Person> result = new ArrayList<>();
        Instant start = Instant.now();
        List<Integer> ids = restTemplate.getForObject("/person", List.class);
        ids.forEach(id -> result.add(restTemplate.getForObject("/person/{id}", Person.class, id)));
        logTime(start);
        return result;
    }


    @GetMapping(value = "/backend/people/reactive")
    public Flux<Person> getBackendPeopleReactive() {
        Instant start = Instant.now();
        return webClient.get().uri("/person").retrieve().bodyToFlux(Integer.class)
                .flatMap(id -> 
                        Mono.zip(
                            webClient.get().uri("/person/{id}", id).retrieve().bodyToMono(Person.class),
                            webClient.get().uri("/person/{id}/domain", id).retrieve().bodyToMono(String.class),
                            (person, domain) -> new Person(person.id(), person.name(), domain)
                        )
                )
                .doFinally(type -> logTime(start));
    }
    
    @GetMapping(value = "/t")
    public ProvideUserRightsAccessRS getTest() {
        ProvideUserRightsAccessRQ request = new ProvideUserRightsAccessRQ();
        request.setUserId("m408461");
        
        return Mono.<ProvideUserRightsAccessRS>create(sink -> provider.provideUserRightsAccessAsync(request, res -> {
            try {
                sink.success(res.get());
            } catch (InterruptedException | ExecutionException e1) {
                sink.error(e1);
            }
        })).block();
    }

    
    @GetMapping(value = "/w000479")
    public Mono<ProvideUserRightsAccessRS> getW000479() {
        ProvideUserRightsAccessRQ request = new ProvideUserRightsAccessRQ();
        request.setUserId("m408461");
//        try {
//            return provider.provideUserRightsAccessAsync(request).get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            return null;
//        }
        
        return Mono.<ProvideUserRightsAccessRS>create(sink -> provider.provideUserRightsAccessAsync(request, res -> {
            try {
                sink.success(res.get(1, TimeUnit.MILLISECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                sink.error(e);
            }
        }));
    }

    private void logTime(Instant start) {
        LOGGER.info(">>> Duration: " + Duration.between(start, Instant.now()).toMillis() + "ms");
    }

    static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            LOGGER.info(clientRequest.url().toString());
            return Mono.just(clientRequest);
        });
    }
}

record Person(Long id, String name, String domain) {
    Person(Long id, String name) {
        this(id, name, null);
    }
}