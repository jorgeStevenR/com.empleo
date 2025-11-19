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
public class JobController {

    private final JobService service;
    private final ApplicationRepository applicationRepository;

    public JobController(JobService service, ApplicationRepository applicationRepository) {
        this.service = service;
        this.applicationRepository = applicationRepository;
    }

    @PostMapping
    public ResponseEntity<Job> create(@RequestBody Job job) {
        return ResponseEntity.ok(service.save(job));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Job updated) {
        Optional<Job> existing = service.findById(id);
        if (existing.isEmpty())
            return ResponseEntity.notFound().build();

        Job job = existing.get();
        job.setTitle(updated.getTitle());
        job.setDescription(updated.getDescription());
        job.setLocation(updated.getLocation());
        job.setCompany(updated.getCompany());
        job.setMode(updated.getMode());
        job.setSalary(updated.getSalary());

        return ResponseEntity.ok(service.save(job));
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Empleo eliminado correctamente");
    }

    @GetMapping("/company/{idCompany}")
    public ResponseEntity<List<Job>> findByCompany(@PathVariable Long idCompany) {
        List<Job> jobs = service.findByCompany(idCompany);
        return jobs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(jobs);
    }

    @GetMapping("/{idJob}/applications")
    public ResponseEntity<?> findApplicationsByJob(@PathVariable Long idJob) {
        List<Application> apps = applicationRepository.findByJob_IdJob(idJob);
        return apps.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(apps);
    }
}
