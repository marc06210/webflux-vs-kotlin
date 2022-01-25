package com.afklm.teccse.tutorial.reactive;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class PersonController {

    private Map<Long, Person> people = new HashMap<>();
    private Map<Long, String> domains = new HashMap<>();

    public PersonController() {
        people.put(1L, new Person(1L, "m408461"));
        people.put(2L, new Person(2L, "m312869"));
        people.put(3L, new Person(3L, "m429347"));
        domains.put(1L, "Darts");
        domains.put(2L, "Spring");
        domains.put(3L, "Kube");
    }
    
    @GetMapping("/person")
    public Set<Long> getPersonIds() {
        return people.keySet();
    }

    @GetMapping("/person/{id}")
    public Person getPerson(@PathVariable Long id, @RequestParam(name = "delay",defaultValue = "2") String delay) {
        try {
            Thread.sleep(Integer.parseInt(delay)*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return people.get(Long.valueOf(id));
    }

    @GetMapping("/person/{id}/domain")
    public Mono<String> getPersonDomains(@PathVariable Long id, @RequestParam(name = "delay",defaultValue = "2") String delay) {
        return Mono.delay(Duration.ofSeconds(Integer.parseInt(delay)*2))
                .thenReturn(domains.get(Long.valueOf(id)));
    }
}

record Person(Long id, String name) {
}