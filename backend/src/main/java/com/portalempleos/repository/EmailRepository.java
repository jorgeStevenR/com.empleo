package com.portalempleos.repository;

import com.portalempleos.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    // Buscar por dirección de correo electrónico (para validar duplicados)
    Optional<Email> findByEmail(String email);
}
