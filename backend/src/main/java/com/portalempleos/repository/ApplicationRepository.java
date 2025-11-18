package com.portalempleos.repository;

import com.portalempleos.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByUser_IdUserAndJob_IdJob(Long userId, Long jobId);

    List<Application> findByUser_IdUser(Long userId);

    // 🆕 NUEVO: obtener todas las postulaciones asociadas a un empleoa
    List<Application> findByJob_IdJob(Long jobId);
}
