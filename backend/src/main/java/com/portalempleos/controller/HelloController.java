package com.portalempleos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String home() {
        return "✅ Backend conectado correctamente a Supabase y funcionando en Spring Boot";
    }
}
