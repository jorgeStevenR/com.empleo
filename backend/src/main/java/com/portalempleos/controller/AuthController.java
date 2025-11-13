package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.repository.UserRepository;
import com.portalempleos.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        Map<String, Object> resp = new HashMap<>();
        User user = userRepository.findByEmailEntity_Email(email).orElse(null);

        if (user == null) {
            System.out.println("❌ Usuario no encontrado: " + email);
            resp.put("error", "Usuario no existe");
            return ResponseEntity.status(401).body(resp);
        }

        System.out.println("🔹 Intentando login de: " + email);
        System.out.println("🔹 Password ingresada: " + password);
        System.out.println("🔹 Password en BD: " + user.getPassword());

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("🔹 Coincide: " + matches);

        if (!matches) {
            resp.put("error", "Credenciales inválidas");
            return ResponseEntity.status(401).body(resp);
        }

        String roleName = user.getRole() != null ? user.getRole().name() : "ROLE_USER";
        String token = jwtUtils.generateJwtToken(email, roleName);

        resp.put("token", token);
        resp.put("role", roleName);
        resp.put("userId", user.getIdUser());

        System.out.println("✅ Login correcto: " + email + " con rol " + roleName);
        return ResponseEntity.ok(resp);
    }
}
