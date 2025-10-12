package com.portalempleos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.portalempleos.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
