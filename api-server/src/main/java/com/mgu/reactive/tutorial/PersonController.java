package com.mgu.reactive.tutorial;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestController
public class PersonController {

    private Map<Long, Person> people = new HashMap<>();
    private Map<Long, String> domains = new HashMap<>();

    public PersonController() {
        people.put(1L, new Person(1L, "userX"));
        people.put(2L, new Person(2L, "userY"));
        people.put(3L, new Person(3L, "userZ"));
        domains.put(1L, "Darts");
        domains.put(2L, "Spring");
        domains.put(3L, "Kube");
    }
    
    @GetMapping("/persons")
    public Set<Long> getPersonIds() {
        return people.keySet();
    }

    @GetMapping("/persons/{id}")
    public Person getPerson(@PathVariable Long id, @RequestParam(name = "delay", required = false) String delay) {
        if (delay!=null) {
            try {
                Thread.sleep(Integer.parseInt(delay)*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return people.get(Long.valueOf(id));
    }

    @GetMapping("/persons/{id}/domain")
    public Mono<String> getPersonDomains(@PathVariable Long id, @RequestParam(name = "delay",required = false) String delay) {
        return Optional.ofNullable (delay)
            .map ( d -> Mono.delay (Duration.ofSeconds(Integer.parseInt(d)*2))
                .thenReturn (domains.get (Long.valueOf(id))))
            .orElse (Mono.just (domains.get(Long.valueOf(id))));
    }
}

record Person(Long id, String name) { }