package com.portalempleos.service;

import com.portalempleos.model.Application;
import com.portalempleos.model.enums.ApplicationStatus;
import com.portalempleos.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Application> findByUserId(Long userId) {
        return repo.findByUser_IdUser(userId);
    }

    public Optional<Application> findByUserAndJob(Long userId, Long jobId) {
        return repo.findByUser_IdUserAndJob_IdJob(userId, jobId);
    }

    @Transactional
    public Application updateStatus(Long id, ApplicationStatus newStatus) {

        Application app = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada con id " + id));

        ApplicationStatus current = app.getStatus();

        switch (current) {
            case PENDING -> {
                // Ahora sí permite aprobar o rechazar directamente
                if (!(newStatus == ApplicationStatus.IN_PROGRESS ||
                      newStatus == ApplicationStatus.ACCEPTED ||
                      newStatus == ApplicationStatus.REJECTED ||
                      newStatus == ApplicationStatus.CANCELED)) {

                    throw new IllegalArgumentException(
                            "Transición no permitida desde PENDING a " + newStatus
                    );
                }
            }
            case IN_PROGRESS -> {
                if (!(newStatus == ApplicationStatus.ACCEPTED ||
                      newStatus == ApplicationStatus.REJECTED)) {

                    throw new IllegalArgumentException(
                            "Transición no permitida desde IN_PROGRESS a " + newStatus
                    );
                }
            }
            default -> throw new IllegalArgumentException("La postulación ya está en estado final: " + current);
        }

        app.setStatus(newStatus);
        return repo.save(app);
    }

    public List<Application> findByJobId(Long jobId) {
        return repo.findByJob_IdJob(jobId);
    }

}
