package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // Crear nuevo usuario (registro)
    @PostMapping
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User saved = service.registerUser(user);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> existing = service.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        User user = existing.get();
        user.setName(updatedUser.getName());
        user.setPassword(updatedUser.getPassword());
        user.setRole(updatedUser.getRole());
        user.setCompany(updatedUser.getCompany());

        return ResponseEntity.ok(service.save(user));
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<User> user = service.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Buscar por email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable String email) {
        Optional<User> user = service.findByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = service.delete(id);
        return deleted ? ResponseEntity.ok("Usuario eliminado correctamente") : ResponseEntity.notFound().build();
    }
}
