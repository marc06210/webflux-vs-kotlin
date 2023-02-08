package com.mgu.reactive.tutorial;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mgu.reactive.tutorial.entity.User;
import com.mgu.reactive.tutorial.entity.UserRepository;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping(value = "/users")
    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        userRepository.findAll().forEach(result::add);
        return result;
    }

}
