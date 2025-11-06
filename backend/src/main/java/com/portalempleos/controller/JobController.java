package com.portalempleos.controller;

import com.portalempleos.model.Job;
import com.portalempleos.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService service;

    public JobController(JobService service) {
        this.service = service;
    }

    // Crear empleo
    @PostMapping
    public ResponseEntity<Job> create(@RequestBody Job job) {
        return ResponseEntity.ok(service.save(job));
    }

    // Actualizar empleo
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Job updated) {
        Optional<Job> existing = service.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        Job job = existing.get();
        job.setTitle(updated.getTitle());
        job.setDescription(updated.getDescription());
        job.setLocation(updated.getLocation());
        job.setCompany(updated.getCompany());

        return ResponseEntity.ok(service.save(job));
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar empleo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Empleo eliminado correctamente");
    }
}
