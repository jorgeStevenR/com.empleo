package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.model.User;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.repository.UserRepository;
import com.portalempleos.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository, CompanyRepository companyRepository,
                          PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "").toLowerCase();
        String password = body.getOrDefault("password", "");

        Map<String, Object> resp = new HashMap<>();

        // Usuario
        User user = userRepository.findByEmailEntity_Email(email).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtils.generateJwtToken(email, user.getRole());
            resp.put("token", token);
            resp.put("role", user.getRole());
            resp.put("userId", user.getIdUser());
            return ResponseEntity.ok(resp);
        }

        // Empresa
        Company company = companyRepository.findByEmailEntity_Email(email).orElse(null);
        if (company != null && passwordEncoder.matches(password, company.getPassword())) {
            String token = jwtUtils.generateJwtToken(email, "COMPANY");
            resp.put("token", token);
            resp.put("role", "COMPANY");
            resp.put("userId", company.getIdCompany());
            return ResponseEntity.ok(resp);
        }

        resp.put("message", "Credenciales incorrectas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
    }
}
