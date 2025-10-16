package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.model.Job;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.repository.JobRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    public JobController(JobRepository jobRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
    }

    // ===== Helpers =====
    private static String ns(String s){ return s == null ? "" : s; }

    private Map<String,Object> toDto(Job j){
        Company c = j.getCompany();
        return Map.of(
            "id", j.getIdJob(),
            "id_job", j.getIdJob(),
            "title", ns(j.getTitle()),
            "description", ns(j.getDescription()),
            "location", ns(j.getLocation()),
            "createdAt", j.getCreatedAt() == null ? null : j.getCreatedAt().toString(),
            "companyDisplay", c != null ? ns(c.getName()) : "",
            "company", c != null ? ns(c.getName()) : "",
            "companyId", c != null ? c.getIdCompany() : null,
            "companyNit", c != null ? c.getNit() : null
        );
    }

    // ===== Público: listar (con q opcional)
    @GetMapping
    public List<Map<String,Object>> list(@RequestParam(name="q", required=false) String q){
        final String term = q == null ? "" : q.trim().toLowerCase();
        List<Job> base = jobRepository.findAllByOrderByCreatedAtDesc();
        if (StringUtils.hasText(term)) {
            base = base.stream().filter(j -> {
                String title = ns(j.getTitle()).toLowerCase();
                String comp  = j.getCompany() != null ? ns(j.getCompany().getName()).toLowerCase() : "";
                return title.contains(term) || comp.contains(term);
            }).collect(Collectors.toList());
        }
        return base.stream().map(this::toDto).collect(Collectors.toList());
    }

    // ===== Crear empleo
    @PostMapping
    public Map<String,Object> createJob(@RequestBody Map<String,Object> body){
        Job j = new Job();
        j.setTitle((String) body.getOrDefault("title",""));
        j.setDescription((String) body.getOrDefault("description",""));
        j.setLocation((String) body.getOrDefault("location",""));

        // Vincular empresa por NIT o por ID
        Object nitObj = body.get("companyNit");
        if (nitObj instanceof String nit && StringUtils.hasText(nit)) {
            companyRepository.findByNit(nit).ifPresent(j::setCompany);
        } else {
            Object idObj = body.get("companyId");
            if (idObj instanceof Number num) {
                companyRepository.findById(num.longValue()).ifPresent(j::setCompany);
            }
        }

        Job saved = jobRepository.save(j);
        return toDto(saved);
    }

    // ===== Empleos por empresa
    @GetMapping("/by-company/{companyId}")
    public List<Map<String,Object>> byCompany(@PathVariable Long companyId){
        return jobRepository.findByCompany_IdCompany(companyId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ===== Eliminar empleo
    @DeleteMapping("/{id}")
    public Map<String,Object> deleteJob(@PathVariable Long id){
        jobRepository.deleteById(id);
        return Map.of("ok", true, "message", "Job deleted", "id", id);
    }
}
