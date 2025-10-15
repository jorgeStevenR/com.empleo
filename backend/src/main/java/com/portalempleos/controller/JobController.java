package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.model.Job;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.repository.JobRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin("*")
public class JobController {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    public JobController(JobRepository jobRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Job> getOne(@PathVariable Long id) {
        return jobRepository.findById(id);
    }

    @PostMapping
    public Job createJob(@RequestBody Map<String, Object> body) {
        Job j = new Job();
        j.setTitle((String) body.get("title"));
        j.setDescription((String) body.get("description"));
        j.setLocation((String) body.get("location"));

        // legacy texto (visible para el público)
        if (body.containsKey("company")) {
            j.setCompany((String) body.get("company"));
        }

        // Preferir companyNit
        Object nitObj = body.get("companyNit");
        if (nitObj instanceof String nit && !nit.isBlank()) {
            companyRepository.findByNit(nit).ifPresent(j::setCompanyEntity);
        } else {
            // compatible: companyId
            Object idObj = body.get("companyId");
            if (idObj instanceof Number num) {
                companyRepository.findById(num.longValue()).ifPresent(j::setCompanyEntity);
            }
        }
        return jobRepository.save(j);
    }

    @PutMapping("/{id}")
    public Job updateJob(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Job j = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));

        if (body.containsKey("title")) j.setTitle((String) body.get("title"));
        if (body.containsKey("description")) j.setDescription((String) body.get("description"));
        if (body.containsKey("location")) j.setLocation((String) body.get("location"));
        if (body.containsKey("company")) j.setCompany((String) body.get("company")); // legacy

        // Preferir NIT
        Object nitObj = body.get("companyNit");
        if (nitObj instanceof String nit && !nit.isBlank()) {
            Company c = companyRepository.findByNit(nit).orElse(null);
            j.setCompanyEntity(c);
        } else {
            Object idObj = body.get("companyId");
            if (idObj instanceof Number num) {
                Company c = companyRepository.findById(num.longValue()).orElse(null);
                j.setCompanyEntity(c);
            }
        }

        return jobRepository.save(j);
    }

    @DeleteMapping("/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobRepository.deleteById(id);
        return "Job deleted";
    }

    @GetMapping("/by-company/{companyId}")
    public List<Job> byCompany(@PathVariable Long companyId) {
        return jobRepository.findByCompanyEntity_IdCompany(companyId);
    }
}
