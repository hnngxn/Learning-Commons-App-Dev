package com.group3.learningcommonsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DbApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DbApplication.class, args);

    }
}
