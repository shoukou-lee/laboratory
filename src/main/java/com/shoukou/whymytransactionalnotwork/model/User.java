package com.shoukou.whymytransactionalnotwork.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Getter
    String name;

    public User(String name) {
        this.name = name;
    }
}
