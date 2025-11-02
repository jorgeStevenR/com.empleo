package com.portalempleos.controller;

import com.portalempleos.model.Email;
import com.portalempleos.service.EmailService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "*")
public class EmailController {
    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @GetMapping
    public List<Email> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Email getById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public Email create(@RequestBody Email email) {
        return service.save(email);
    }

    @PutMapping("/{id}")
    public Email update(@PathVariable Long id, @RequestBody Email email) {
        email.setIdEmail(id);
        return service.save(email);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
