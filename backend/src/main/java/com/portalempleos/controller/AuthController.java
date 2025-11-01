package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.security.JwtUtil;
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
        String username = body.get("username");
        String password = body.get("password");

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        String role = auth.getAuthorities().stream().findFirst().map(Object::toString).orElse("USER");
        String token = jwtUtil.generateToken(username, role, Map.of());

        return ResponseEntity.ok(Map.of("ok", true, "token", token, "role", role));
    }

    @PostMapping("/company/register")
    public ResponseEntity<?> registerCompany(@RequestBody Company c) {
        Company saved = companyService.register(c, c.getEmailEntity() != null ? c.getEmailEntity().getEmail() : null);
        saved.setPassword(null);
        return ResponseEntity.ok(Map.of("ok", true, "company", saved));
    }
}
