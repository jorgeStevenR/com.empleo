package com.portalempleos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // Verificación rápida del backend
    @GetMapping({"/", "/ping"})
    public String ping() {
        return "Backend OK";
    }

    @GetMapping("/job")
    public String jobs() {
        return "redirect:/job.html";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "redirect:/register.html";
    }
}
