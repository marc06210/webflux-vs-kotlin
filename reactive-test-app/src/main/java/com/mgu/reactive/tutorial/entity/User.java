package com.mgu.reactive.tutorial.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "mgu_user")
public class User {
    @Id
    private Long id;
    private String name;
 
    public Long getId() {
        return id;
    }
 
    public User() {
    }
    
    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() {
        return "User: " + id + "/" + name ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }
}