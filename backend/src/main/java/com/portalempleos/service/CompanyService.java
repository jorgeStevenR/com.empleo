package com.portalempleos.service;

import com.portalempleos.model.Company;
import com.portalempleos.model.Email;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.repository.EmailRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;

    public CompanyService(CompanyRepository companyRepository,
                          EmailRepository emailRepository,
                          PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.emailRepository = emailRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra una nueva compañía con email único.
     */
    public Company register(Company c, String emailAddress) {
        Email email = emailRepository.findByEmail(emailAddress.toLowerCase())
                .orElseGet(() -> emailRepository.save(new Email(emailAddress.toLowerCase())));
        c.setEmailEntity(email);

        if (c.getPassword() != null && !c.getPassword().startsWith("$2a$")) {
            c.setPassword(passwordEncoder.encode(c.getPassword()));
        }
        return companyRepository.save(c);
    }

    public Optional<Company> getByNit(String nit) {
        return companyRepository.findByNit(nit);
    }

    public Optional<Company> getById(Long id) {
        return companyRepository.findById(id);
    }

    public Optional<Company> getByEmail(String email) {
        return companyRepository.findAll().stream()
                .filter(c -> c.getEmailEntity() != null &&
                        email.equalsIgnoreCase(c.getEmailEntity().getEmail()))
                .findFirst();
    }

    public boolean checkPassword(Company c, String raw) {
        return c != null && raw != null && passwordEncoder.matches(raw, c.getPassword());
    }
}
