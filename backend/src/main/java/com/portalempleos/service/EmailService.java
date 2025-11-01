package com.portalempleos.service;

import com.portalempleos.model.Email;
import com.portalempleos.repository.EmailRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EmailService {

    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    /**
     * Busca un email por dirección, o lo crea si no existe.
     */
    public Email getOrCreate(String address) {
        return emailRepository.findByEmail(address.toLowerCase())
                .orElseGet(() -> emailRepository.save(new Email(address.toLowerCase())));
    }

    public Optional<Email> findByEmail(String address) {
        return emailRepository.findByEmail(address.toLowerCase());
    }
}
