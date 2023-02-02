package com.mgu.reactive.tutorial.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class ReactiveFacade {

    @Autowired
    private UserRepository userRepository;
    
    public Mono<User> findById(Long id) {
        return Mono.fromCallable(() -> userRepository.findById(id).orElseThrow());
    }
}
