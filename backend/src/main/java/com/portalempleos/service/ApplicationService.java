package com.portalempleos.service;

import com.portalempleos.model.Application;
import com.portalempleos.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    private final ApplicationRepository repo;

    public ApplicationService(ApplicationRepository repo) {
        this.repo = repo;
    }

    public List<Application> findAll() {
        return repo.findAll();
    }

    public Optional<Application> findById(Long id) {
        return repo.findById(id);
    }

    public Application save(Application a) {
        return repo.save(a);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    // ✅ Nuevo: buscar postulaciones por usuario
    public List<Application> findByUserId(Long userId) {
        return repo.findByUser_IdUser(userId);
    }

    // ✅ Nuevo: verificar si ya existe una postulación a ese empleo por ese usuario
    public Optional<Application> findByUserAndJob(Long userId, Long jobId) {
        return repo.findByUser_IdUserAndJob_IdJob(userId, jobId);
    }
}
