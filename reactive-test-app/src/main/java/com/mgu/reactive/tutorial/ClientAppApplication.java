package com.mgu.reactive.tutorial;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import com.mgu.reactive.tutorial.entity.User;
import com.mgu.reactive.tutorial.entity.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ClientAppApplication {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAppApplication.class);
    
    @Autowired
    private UserRepository userRepository;
    public static void main(String[] args) {
        SpringApplication.run(ClientAppApplication.class, args);
    }
    
  //private static final List<User> users = Arrays.asList(new User(1L, "Marc"), new User(2L, "Eric"), new User(3L, "Amélie"));
    private static final String[] userNames = "Marc,Eric,Amélie".split(",");
    private final AtomicLong counter = new AtomicLong(1);
    
//    @EventListener(ApplicationReadyEvent.class)
//    public void loadDatabase() {
//        Flux.fromArray(userNames)
//            .map(name -> new User(counter.getAndIncrement(), name))
//            .map(userRepository::save)
//            .doOnComplete(() -> LOGGER.info(">>> LOAD DATABASE OVER <<<"))
//          .subscribe();
////        Flux.fromIterable(users)
////        .map(userRepository::save)
////        .doOnComplete(() -> LOGGER.info(">>> LOAD DATABASE OVER <<<"))
////        .subscribe()
////        ;
//    }
    
    @Bean
    ApplicationListener<ApplicationReadyEvent> initMgu() {
        //return (are) -> { System.out.println("\n\n\n MGU \n\n\n"); };
        return are -> Flux.fromArray(userNames)
                .map(name -> new User(counter.getAndIncrement(), name))
                .map(userRepository::save)
                .doOnComplete(() -> LOGGER.info(">>> LOAD DATABASE OVER <<<"))
              .subscribe();
    }
    
    
    
    
//    @EventListener(ApplicationReadyEvent.class)
    public void test() {
        LOGGER.info(">>> starting");
        Mono<String> sMono = Mono.just("ok").delayElement(Duration.ofSeconds(5));
        Mono<String> sSeq = Mono.fromCallable(() -> doSequentialStuf());
        
        String result = Mono.zip(sMono,
                sSeq, 
                (s1, s2) -> s1 + s2)
                .block();
        LOGGER.info(">>> result = " + result);
    }
    
    public static String doSequentialStuf() {
        LOGGER.info("dosequential");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        }
        LOGGER.info("sequential");
        return "sequential";
    }

}
