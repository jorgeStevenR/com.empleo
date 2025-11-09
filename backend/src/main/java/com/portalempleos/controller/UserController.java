package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.service.UserService;
import com.portalempleos.service.FileStorageService;
import com.portalempleos.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorage;
    private final UserRepository userRepository;

    public UserController(UserService service,
            PasswordEncoder passwordEncoder,
            FileStorageService fileStorage,
            UserRepository userRepository) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.fileStorage = fileStorage;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            return ResponseEntity.ok(service.registerUser(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> existingOpt = service.findById(id);
        if (existingOpt.isEmpty())
            return ResponseEntity.notFound().build();

        User existing = existingOpt.get();
        existing.setName(updatedUser.getName());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        existing.setRole(updatedUser.getRole());
        existing.setCompany(updatedUser.getCompany());

        return ResponseEntity.ok(service.save(existing));
    }

    // Upload CV (PDF). Solo el propietario (o ADMIN) puede subirlo.
    @PutMapping(path = "/{id}/cv", consumes = { "multipart/form-data" })
    public ResponseEntity<?> uploadCv(@PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            Authentication auth) {
        Optional<User> opt = service.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        String emailFromAuth = (auth != null) ? auth.getName().toLowerCase() : null;
        if (emailFromAuth == null)
            return ResponseEntity.status(401).body("No autenticado");

        User target = opt.get();
        String targetEmail = target.getEmailEntity().getEmail().toLowerCase();

        boolean isAdmin = userRepository.findByEmailEntity_Email(emailFromAuth)
                .map(u -> "ADMIN".equalsIgnoreCase(u.getRole()))
                .orElse(false);

        if (!isAdmin && !targetEmail.equals(emailFromAuth)) {
            return ResponseEntity.status(403).body("No puedes subir el CV de otro usuario");
        }

        try {
            String url = fileStorage.save(file, "cv", Set.of("application/pdf"), "pdf");
            target.setCvUrl(url);
            return ResponseEntity.ok(service.save(target));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error subiendo CV: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable String email) {
        return service.findByEmail(email).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.ok("Usuario eliminado correctamente")
                : ResponseEntity.notFound().build();
    }
}
