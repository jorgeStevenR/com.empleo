package com.portalempleos.controller;

import com.portalempleos.model.Application;
import com.portalempleos.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    // ✅ Crear postulación (evita duplicados)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Application app) {
        Long userId = app.getUser().getIdUser();
        Long jobId = app.getJob().getIdJob();

        // Verificar si ya existe
        Optional<Application> existing = service.findByUserAndJob(userId, jobId);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("⚠️ Ya estás postulado a esta oferta.");
        }

        Application saved = service.save(app);
        return ResponseEntity.ok(saved);
    }

    // 🔹 Actualizar postulación
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

    // 🔹 Listar todas
    @GetMapping
    public ResponseEntity<List<Application>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // 🔹 Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Nuevo: buscar por usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Application>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    // 🔹 Eliminar postulación
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("🗑️ Postulación eliminada correctamente.");
    }
}
