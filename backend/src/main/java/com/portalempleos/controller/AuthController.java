package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.security.JwtUtil;
import com.portalempleos.security.PortalUserDetailsService;
import com.portalempleos.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final CompanyService companyService;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          CompanyService companyService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.companyService = companyService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username"); // email de usuario o NIT de empresa
        String password = body.get("password");

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        String role = auth.getAuthorities().stream().findFirst().map(Object::toString).orElse("USER");
        String token = jwtUtil.generateToken(username, role, Map.of());

        return ResponseEntity.ok(Map.of("ok", true, "token", token, "role", role));
    }

    // Registro rápido de empresas (opcional)
    @PostMapping("/company/register")
    public ResponseEntity<?> registerCompany(@RequestBody Company c) {
        if (c.getNit() == null || c.getNit().isBlank() ||
            c.getPassword() == null || c.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "NIT y password son obligatorios"));
        }
        Company saved = companyService.register(c);
        // Por seguridad, no retornamos el hash
        saved.setPassword(null);
        return ResponseEntity.ok(Map.of("ok", true, "company", saved));
    }
}
