package com.portalempleos.service;

import com.portalempleos.model.Job;
import com.portalempleos.repository.JobRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository repo;

    public JobService(JobRepository repo) {
        this.repo = repo;
    }

    public List<Job> findAll() {
        return repo.findAll();
    }

    public Optional<Job> findById(Long id) {
        return repo.findById(id);
    }

    public Job save(Job j) {
        return repo.save(j);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Job> findByCompany(Long idCompany) {
        return repo.findByCompany_IdCompany(idCompany);
    }
}
