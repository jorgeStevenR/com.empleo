package com.portalempleos.controller;

import com.portalempleos.model.Application;
import com.portalempleos.model.Job;
import com.portalempleos.repository.ApplicationRepository;
import com.portalempleos.repository.JobRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/company-dashboard")
@CrossOrigin(origins = "*")
public class CompanyDashboardController {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    public CompanyDashboardController(ApplicationRepository applicationRepository,
                                      JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
    }

    // Empleos de la compañía
    @GetMapping("/{companyId}/jobs")
    public List<Map<String,Object>> jobs(@PathVariable Long companyId){
        List<Job> list = jobRepository.findByCompany_IdCompany(companyId);
        return list.stream()
                .map(j -> {
                    Map<String,Object> m = new HashMap<>();
                    m.put("id", j.getIdJob());
                    m.put("title", j.getTitle());
                    m.put("location", j.getLocation());
                    m.put("description", Optional.ofNullable(j.getDescription()).orElse(""));
                    return m;
                })
                .collect(Collectors.toList());
    }

    // Postulaciones de la compañía (todas)
    @GetMapping("/{companyId}/applications")
    public List<Map<String,Object>> applications(@PathVariable Long companyId){
        List<Application> list = applicationRepository.findByJob_Company_IdCompany(companyId);
        List<Map<String,Object>> out = new ArrayList<>();
        for (Application a : list){
            Map<String,Object> m = new HashMap<>();
            m.put("id", a.getIdApplication());
            m.put("status", a.getStatus().name());
            m.put("appliedAt", a.getAppliedAt());

            if (a.getUser()!=null){
                Map<String,Object> u = new HashMap<>();
                u.put("id", a.getUser().getIdUser());
                u.put("name", a.getUser().getName());
                u.put("email", a.getUser().getEmail());
                m.put("user", u);
            }
            if (a.getJob()!=null){
                Map<String,Object> j = new HashMap<>();
                j.put("id", a.getJob().getIdJob());
                j.put("title", a.getJob().getTitle());
                m.put("job", j);
            }
            out.add(m);
        }
        return out;
    }

    // Conteos por estado para badges/gráficas
    @GetMapping("/{companyId}/counts")
    public Map<String,Object> counts(@PathVariable Long companyId){
        long total = applicationRepository.countByJob_Company_IdCompany(companyId);
        long pend  = applicationRepository.countByJob_Company_IdCompanyAndStatus(companyId, Application.Status.Pendiente);
        long revi  = applicationRepository.countByJob_Company_IdCompanyAndStatus(companyId, Application.Status.Revisando);
        long rech  = applicationRepository.countByJob_Company_IdCompanyAndStatus(companyId, Application.Status.Rechazado);
        long acpt  = applicationRepository.countByJob_Company_IdCompanyAndStatus(companyId, Application.Status.Aceptado);

        Map<String,Object> m = new HashMap<>();
        m.put("companyId", companyId);
        m.put("total", total);
        m.put("Pendiente", pend);
        m.put("Revisando", revi);
        m.put("Rechazado", rech);
        m.put("Aceptado", acpt);
        return m;
    }
}
