package com.portalempleos.service;

import com.portalempleos.model.Email;
import com.portalempleos.model.User;
import com.portalempleos.repository.EmailRepository;
import com.portalempleos.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder; // Para encriptar contraseñas

    // Constructor con inyección de dependencias
    public UserService(UserRepository userRepository,
                       EmailRepository emailRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar nuevo usuario con validación y encriptación
    @Transactional
    public User registerUser(User user) {
        String emailText = user.getEmailEntity().getEmail().toLowerCase();

        // Validar si el correo ya existe
        Optional<Email> existing = emailRepository.findByEmail(emailText);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("El correo '" + emailText + "' ya está registrado.");
        }

        // Crear el email
        Email email = new Email();
        email.setEmail(emailText);
        emailRepository.save(email);

        // 🔐 Encriptar contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailEntity(email);

        return userRepository.save(user);
    }

    // Actualizar usuario
    public User save(User user) {
        return userRepository.save(user);
    }

    // Eliminar usuario
    public boolean delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Buscar por email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailEntity_Email(email.toLowerCase());
    }

    // Listar todos
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    // Buscar por ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
