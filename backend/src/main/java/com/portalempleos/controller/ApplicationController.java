package com.portalempleos.controller;

import com.portalempleos.model.Application;
import com.portalempleos.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    // Crear postulación
    @PostMapping
    public ResponseEntity<Application> create(@RequestBody Application app) {
        return ResponseEntity.ok(service.save(app));
    }

    // Actualizar postulación
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Application updated) {
        Optional<Application> existing = service.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        Application app = existing.get();
        app.setCoverLetter(updated.getCoverLetter());
        app.setUrlImg(updated.getUrlImg());
        app.setStatus(updated.getStatus());
        app.setUser(updated.getUser());
        app.setJob(updated.getJob());

        return ResponseEntity.ok(service.save(app));
    }

    // Listar todas
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

    // Eliminar postulación
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Postulación eliminada correctamente");
    }
}
