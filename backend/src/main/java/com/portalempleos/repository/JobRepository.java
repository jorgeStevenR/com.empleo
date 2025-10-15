package com.portalempleos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.portalempleos.model.Job;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    // nuevos: listar por empresa
    List<Job> findByCompanyEntity_IdCompany(Long idCompany);
}
