package com.portalempleos.controller;

import com.portalempleos.model.Job;
import com.portalempleos.model.Application;
import com.portalempleos.service.JobService;
import com.portalempleos.repository.ApplicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService service;
    private final ApplicationRepository applicationRepository;

    public JobController(JobService service, ApplicationRepository applicationRepository) {
        this.service = service;
        this.applicationRepository = applicationRepository;
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
        job.setType(updated.getType());

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

    // 🆕 NUEVO: Listar empleos por empresa
    @GetMapping("/company/{idCompany}")
    public ResponseEntity<List<Job>> findByCompany(@PathVariable Long idCompany) {
        List<Job> jobs = service.findByCompany(idCompany);
        if (jobs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(jobs);
    }

    // 🆕 NUEVO: Obtener postulaciones de una vacante específica
    @GetMapping("/{idJob}/applications")
    public ResponseEntity<?> findApplicationsByJob(@PathVariable Long idJob) {
        List<Application> apps = applicationRepository.findByJob_IdJob(idJob);
        if (apps.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(apps);
    }
}
