package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.model.enums.Role;
import com.portalempleos.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<User> opt = userRepository.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Actualizar usuario solo si es el mismo o ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User updatedUser,
            @RequestHeader("emailFromAuth") String emailFromAuth) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        User target = opt.get();
        String targetEmail = target.getEmailEntity().getEmail().toLowerCase();

        // ✅ Detectar si el usuario autenticado es ADMIN
        boolean isAdmin = userRepository.findByEmailEntity_Email(emailFromAuth)
                .map(u -> u.getRole() == Role.ROLE_ADMIN)
                .orElse(false);

        // ✅ Bloquear si no es admin y quiere editar otro perfil
        if (!isAdmin && !targetEmail.equalsIgnoreCase(emailFromAuth)) {
            return ResponseEntity.status(403).body("No autorizado para modificar este usuario");
        }

        target.setName(updatedUser.getName());
        target.setCvUrl(updatedUser.getCvUrl());
        userRepository.save(target);

        return ResponseEntity.ok(target);
    }
}
