package com.portalempleos.repository;

import com.portalempleos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por correo (relación con la tabla emails)
    Optional<User> findByEmailEntity_Email(String email);
}
