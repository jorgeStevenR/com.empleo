package com.portalempleos.controller;

import com.portalempleos.model.Application;
import com.portalempleos.model.enums.ApplicationStatus;
import com.portalempleos.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Application app) {
        Long userId = app.getUser().getIdUser();
        Long jobId = app.getJob().getIdJob();
        Optional<Application> existing = service.findByUserAndJob(userId, jobId);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("⚠️ Ya estás postulado a esta oferta.");
        }
        return ResponseEntity.ok(service.save(app));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Application updated) {
        Optional<Application> existing = service.findById(id);
        if (existing.isEmpty())
            return ResponseEntity.notFound().build();

        Application app = existing.get();
        app.setCoverLetter(updated.getCoverLetter());
        app.setStatus(updated.getStatus());
        app.setUser(updated.getUser());
        app.setJob(updated.getJob());
        return ResponseEntity.ok(service.save(app));
    }

    // Cambiar solo el estado con reglas
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestBody StatusRequest body) {
        try {
            ApplicationStatus s = ApplicationStatus.valueOf(body.status().toUpperCase());
            Application updated = service.updateStatus(id, s);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record StatusRequest(String status) {
    }

    @GetMapping
    public ResponseEntity<List<Application>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Application>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("🗑️ Postulación eliminada correctamente.");
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> findByJob(@PathVariable Long jobId) {
    return ResponseEntity.ok(service.findByJobId(jobId));
}
}
