package com.portalempleos.repository;

import com.portalempleos.model.Application;
import com.portalempleos.model.Application.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Búsquedas directas
    List<Application> findByJob_IdJob(Long jobId);
    List<Application> findByUser_IdUser(Long userId);
    List<Application> findByStatus(Status status);

    // Traversal por la relación Application -> Job -> Company
    List<Application> findByJob_Company_IdCompany(Long idCompany);

    // Contadores por compañía
    long countByJob_Company_IdCompany(Long idCompany);

    // Contadores por compañía y estado
    long countByJob_Company_IdCompanyAndStatus(Long idCompany, Status status);
}
