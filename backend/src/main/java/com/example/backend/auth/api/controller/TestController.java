package com.example.backend.auth.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public String test() {
        return "test!!";
    }

    @GetMapping("/auth/test")
    public String login() {
        return "test!!";
    }
}
