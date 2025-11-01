package com.portalempleos.repository;

import com.portalempleos.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Optional<Email> findByEmail(String email);
}
