package com.portalempleos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Redirige raíz y /ping a index.html
    @GetMapping({"/", "/ping"})
    public String home() {
        return "redirect:/index.html";
    }

    // Redirige /job -> job.html
    @GetMapping("/job")
    public String jobs() {
        return "redirect:/job.html";
    }

    // Redirige /login -> login.html
    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    // Redirige /register -> register.html
    @GetMapping("/register")
    public String register() {
        return "redirect:/register.html";
    }
}
