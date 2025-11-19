package com.portalempleos.service;

import com.portalempleos.model.Company;
import com.portalempleos.model.Email;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.repository.EmailRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;

    public CompanyService(CompanyRepository companyRepository, EmailRepository emailRepository,
                          PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.emailRepository = emailRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✔ Registrar nueva empresa verificando correo + NIT duplicado
    @Transactional
    public Company registerCompany(Company company) {

        String emailText = company.getEmailEntity().getEmail().toLowerCase();
        String nitText = company.getNit();

        // ✔ Validar NIT único
        if (companyRepository.findByNit(nitText).isPresent()) {
            throw new IllegalArgumentException("El NIT '" + nitText + "' ya está registrado.");
        }

        // ✔ Validar email único
        if (emailRepository.findByEmail(emailText).isPresent()) {
            throw new IllegalArgumentException("El correo '" + emailText + "' ya está registrado.");
        }

        // Crear email asociado
        Email email = new Email();
        email.setEmail(emailText);
        emailRepository.save(email);

        // Codificar contraseña
        company.setPassword(passwordEncoder.encode(company.getPassword()));

        // Rol por defecto
        company.setRole("COMPANY");

        company.setEmailEntity(email);
        return companyRepository.save(company);
    }

    public Company save(Company company) {
        return companyRepository.save(company);
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
