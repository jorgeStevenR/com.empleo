package com.portalempleos.service;

import com.portalempleos.model.Company;
import com.portalempleos.model.Email;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.repository.EmailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final EmailRepository emailRepository;

    public CompanyService(CompanyRepository companyRepository, EmailRepository emailRepository) {
        this.companyRepository = companyRepository;
        this.emailRepository = emailRepository;
    }

    /**
     * ✅ Registrar nueva empresa, verificando que el correo no se repita
     */
    @Transactional
    public Company registerCompany(Company company) {
        String emailText = company.getEmailEntity().getEmail().toLowerCase();

        // 🚫 Verificar si el correo ya existe en cualquier empresa o usuario
        Optional<Email> existing = emailRepository.findByEmail(emailText);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("❌ El correo '" + emailText + "' ya está registrado.");
        }

        // ✅ Crear nuevo email y asignarlo
        Email email = new Email();
        email.setEmail(emailText);
        emailRepository.save(email);

        company.setEmailEntity(email);
        return companyRepository.save(company);
    }

    /**
     * ✅ Listar todas las empresas
     */
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    /**
     * ✅ Buscar una empresa por ID
     */
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    /**
     * ✅ Eliminar una empresa por ID
     */
    @Transactional
    public boolean deleteById(Long id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
