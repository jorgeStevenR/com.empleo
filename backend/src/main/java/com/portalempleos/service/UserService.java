package com.portalempleos.service;

import com.portalempleos.model.Email;
import com.portalempleos.model.User;
import com.portalempleos.repository.EmailRepository;
import com.portalempleos.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       EmailRepository emailRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Registrar nuevo usuario (normalizado)
    public User registerUser(User user, String emailAddress) {
        Email email = emailRepository.findByEmail(emailAddress.toLowerCase())
                .orElseGet(() -> emailRepository.save(new Email(emailAddress.toLowerCase())));
        user.setEmailEntity(email);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Buscar usuario por email (referencia a emails)
    public Optional<User> findByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(u -> u.getEmailEntity() != null &&
                        email.equalsIgnoreCase(u.getEmailEntity().getEmail()))
                .findFirst();
    }

    // Buscar por ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
