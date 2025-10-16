package com.portalempleos.service;

import com.portalempleos.model.Company;
import com.portalempleos.repository.CompanyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public CompanyService(CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Company register(Company c) {
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

    public boolean checkPassword(Company c, String raw) {
        return c != null && raw != null && passwordEncoder.matches(raw, c.getPassword());
    }
}
