package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.model.User;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.repository.UserRepository;
import com.portalempleos.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

        Map<String, Object> response = new HashMap<>();

        // 🔹 1️⃣ Buscar usuario normal
        var userOpt = userRepository.findByEmailEntity_Email(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtUtils.generateJwtToken(email, user.getRole());
                response.put("token", token);
                response.put("role", user.getRole().toString());
                response.put("userId", user.getIdUser()); // ✅ devuelve el ID del usuario
                return response;
            }
        }

        // 🔹 2️⃣ Buscar empresa
        var companyOpt = companyRepository.findAll().stream()
                .filter(c -> c.getEmailEntity().getEmail().equalsIgnoreCase(email))
                .findFirst();

        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            if (passwordEncoder.matches(password, company.getPassword())) {
                String token = jwtUtils.generateJwtToken(email, "COMPANY");
                response.put("token", token);
                response.put("role", "COMPANY");
                response.put("userId", company.getIdCompany()); // ✅ devuelve el ID de la empresa
                return response;
            }
        }

        // 🚫 Si no coincide nada
        throw new RuntimeException("Credenciales incorrectas");
    }
}
