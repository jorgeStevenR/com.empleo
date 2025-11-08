package com.portalempleos.repository;

import com.portalempleos.model.Application;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByUser_IdUserAndJob_IdJob(Long userId, Long jobId);
    List<Application> findByUser_IdUser(Long userId);
}
