package com.group3.learningcommonsproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Screen 01: the home page
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
}