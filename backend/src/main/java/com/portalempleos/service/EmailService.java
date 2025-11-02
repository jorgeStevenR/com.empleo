package com.portalempleos.service;

import com.portalempleos.model.Email;
import com.portalempleos.repository.EmailRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmailService {
    private final EmailRepository repo;

    public EmailService(EmailRepository repo) {
        this.repo = repo;
    }

    public List<Email> findAll() {
        return repo.findAll();
    }

    public Optional<Email> findById(Long id) {
        return repo.findById(id);
    }

    public Email save(Email e) {
        return repo.save(e);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
