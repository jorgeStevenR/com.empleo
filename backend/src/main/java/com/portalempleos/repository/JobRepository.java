package com.portalempleos.repository;

import com.portalempleos.model.Job;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
     List<Job> findByCompany_IdCompany(Long idCompany);
}
