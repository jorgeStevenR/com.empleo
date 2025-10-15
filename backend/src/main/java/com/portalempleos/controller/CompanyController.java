package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.model.Job;
import com.portalempleos.model.Application;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.repository.JobRepository;
import com.portalempleos.repository.ApplicationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin("*")
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public CompanyController(CompanyRepository companyRepository,
                             JobRepository jobRepository,
                             ApplicationRepository applicationRepository) {
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    // CRUD básico
    @GetMapping
    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Company> getOne(@PathVariable Long id) {
        return companyRepository.findById(id);
    }

    @PostMapping
    public Company create(@RequestBody Company c) {
        return companyRepository.save(c);
    }

    @PutMapping("/{id}")
    public Company update(@PathVariable Long id, @RequestBody Company body) {
        return companyRepository.findById(id).map(c -> {
            c.setName(body.getName());
            c.setWebsite(body.getWebsite());
            c.setDescription(body.getDescription());
            c.setLocation(body.getLocation());
            return companyRepository.save(c);
        }).orElseThrow(() -> new RuntimeException("Company not found: " + id));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        companyRepository.deleteById(id);
        return "Company deleted";
    }

    // --- Extras útiles ---

    // Empleos de una empresa
    @GetMapping("/{id}/jobs")
    public List<Job> jobsByCompany(@PathVariable Long id) {
        return jobRepository.findByCompanyEntity_IdCompany(id);
    }

    // Postulaciones de una empresa (vía empleo)
    @GetMapping("/{id}/applications")
    public List<Application> applicationsByCompany(@PathVariable Long id) {
        return applicationRepository.findByJob_CompanyEntity_IdCompany(id);
    }
}
