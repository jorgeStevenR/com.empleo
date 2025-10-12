package com.portalempleos.service;

import com.portalempleos.model.User;
import com.portalempleos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Registrar nuevo usuario
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    // Buscar usuario por email (para login)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Buscar por ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Eliminar usuario
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
