package com.portalempleos.controller;

import com.portalempleos.model.Application;
import com.portalempleos.repository.ApplicationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationRepository repo;

    public ApplicationController(ApplicationRepository repo) {
        this.repo = repo;
    }

    // --- Listar por empleo ---
    @GetMapping("/by-job/{jobId}")
    public List<Application> byJob(@PathVariable Long jobId) {
        return repo.findByJob_IdJob(jobId);
    }

    // --- Listar por usuario ---
    @GetMapping("/by-user/{userId}")
    public List<Application> byUser(@PathVariable Long userId) {
        return repo.findByUser_IdUser(userId);
    }

    // --- Listar por estado ---
    @GetMapping("/by-status/{status}")
    public List<Application> byStatus(@PathVariable Application.Status status) {
        return repo.findByStatus(status);
    }

    // --- Panel empresa: métricas / conteos por compañía ---
    @GetMapping("/counts/company/{companyId}")
    public Map<String, Object> countsByCompany(@PathVariable Long companyId) {
        long total = repo.countByJob_Company_IdCompany(companyId);
        long pend  = repo.countByJob_Company_IdCompanyAndStatus(companyId, Application.Status.Pendiente);
        long revi  = repo.countByJob_Company_IdCompanyAndStatus(companyId, Application.Status.Revisando);
        long rech  = repo.countByJob_Company_IdCompanyAndStatus(companyId, Application.Status.Rechazado);
        long acpt  = repo.countByJob_Company_IdCompanyAndStatus(companyId, Application.Status.Aceptado);

        Map<String,Object> res = new HashMap<>();
        res.put("companyId", companyId);
        res.put("total", total);
        res.put("Pendiente", pend);
        res.put("Revisando", revi);
        res.put("Rechazado", rech);
        res.put("Aceptado", acpt);
        return res;
    }

    // --- (Opcional) Listado de todas las aplicaciones de una compañía ---
    @GetMapping("/by-company/{companyId}")
    public List<Application> byCompany(@PathVariable Long companyId) {
        return repo.findByJob_Company_IdCompany(companyId);
    }
}
