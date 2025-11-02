package com.portalempleos.service;

import com.portalempleos.model.Email;
import com.portalempleos.model.User;
import com.portalempleos.repository.EmailRepository;
import com.portalempleos.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    public UserService(UserRepository userRepository, EmailRepository emailRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
    }

    @Transactional
    public User registerUser(User user) {
        String emailText = user.getEmailEntity().getEmail().toLowerCase();

        // 🚫 Verificar si el correo ya existe
        Optional<Email> existing = emailRepository.findByEmail(emailText);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("❌ El correo '" + emailText + "' ya está registrado.");
        }

        // ✅ Crear nuevo email
        Email email = new Email();
        email.setEmail(emailText);
        emailRepository.save(email);

        user.setEmailEntity(email);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailEntity_Email(email.toLowerCase());
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }
}
