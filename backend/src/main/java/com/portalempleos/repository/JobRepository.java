package com.portalempleos.repository;

import com.portalempleos.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByCompany_IdCompany(Long companyId);
    List<Job> findAllByOrderByCreatedAtDesc();
}
