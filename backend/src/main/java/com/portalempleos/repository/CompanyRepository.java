package com.portalempleos.repository;

import com.portalempleos.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByEmailEntity_Email(String email);

    // ✔ NUEVO: evitar NIT duplicado
    Optional<Company> findByNit(String nit);
}
