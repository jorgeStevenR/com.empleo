package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.model.User;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.repository.UserRepository;
import com.portalempleos.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
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
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String email = body.get("email").toLowerCase();
        String password = body.get("password");

        // Buscar primero si es usuario
        var userOpt = userRepository.findByEmailEntity_Email(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtUtils.generateJwtToken(email, user.getRole());
                return Map.of("token", token, "role", user.getRole());
            }
        }

        // 🔍 Si no, buscar si es empresa
        var companyOpt = companyRepository.findAll().stream()
                .filter(c -> c.getEmailEntity().getEmail().equalsIgnoreCase(email))
                .findFirst();

        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            if (passwordEncoder.matches(password, company.getPassword())) {
                String token = jwtUtils.generateJwtToken(email, "COMPANY");
                return Map.of("token", token, "role", "COMPANY");
            }
        }

        throw new RuntimeException("Credenciales incorrectas");
    }
}
