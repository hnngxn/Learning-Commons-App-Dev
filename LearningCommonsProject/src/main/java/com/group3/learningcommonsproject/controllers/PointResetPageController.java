package com.group3.learningcommonsproject.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PointResetPageController {
    @GetMapping("/point-reset")
    public String pointReset() {
        return "point-reset";
    }
}
