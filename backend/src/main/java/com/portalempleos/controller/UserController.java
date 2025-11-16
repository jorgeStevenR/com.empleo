package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.model.enums.Role;
import com.portalempleos.repository.UserRepository;
import com.portalempleos.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository,
                          UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // =========================================================
    // ✅ REGISTRAR USUARIO (PÚBLICO)
    // =========================================================
    @PostMapping
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Si en el body no viene rol, forzamos ROLE_USER por seguridad
            if (user.getRole() == null) {
                user.setRole(Role.ROLE_USER);
            }

            User saved = userService.registerUser(user);
            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // ✅ OBTENER USUARIO POR ID (Requiere token)
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<User> opt = userRepository.findById(id);
        return opt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =========================================================
    // ✅ ACTUALIZAR USUARIO (Solo él mismo o ADMIN)
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody User updatedUser,
                                    Authentication authentication) {

        // Verificar autenticación
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        String emailFromToken = authentication.getName();

        // Buscar usuario objetivo
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User target = opt.get();
        String targetEmail = target.getEmailEntity().getEmail().toLowerCase();

        // Verificar si el usuario autenticado es admin
        boolean isAdmin = userRepository.findByEmailEntity_Email(emailFromToken)
                .map(u -> u.getRole() == Role.ROLE_ADMIN)
                .orElse(false);

        // Verificar si edita su propio perfil o si es admin
        if (!isAdmin && !targetEmail.equalsIgnoreCase(emailFromToken)) {
            return ResponseEntity.status(403).body("No autorizado para modificar este usuario");
        }

        // Actualizar solo campos permitidos
        target.setName(updatedUser.getName());
        target.setCvUrl(updatedUser.getCvUrl());

        userRepository.save(target);

        return ResponseEntity.ok(target);
    }
}
