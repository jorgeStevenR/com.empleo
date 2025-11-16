package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.model.Company;
import com.portalempleos.repository.UserRepository;
import com.portalempleos.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository,
                          CompanyRepository companyRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        Map<String, Object> resp = new HashMap<>();

        System.out.println("🔍 Buscando usuario o empresa con email: " + email);

        // 1️⃣ Buscar primero en USERS
        User user = userRepository.findByEmailEntity_Email(email).orElse(null);

        if (user != null) {
            System.out.println("🔹 Usuario encontrado en USERS");

            boolean matches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("🔹 Coincide password: " + matches);

            if (!matches) {
                resp.put("error", "Credenciales inválidas");
                return ResponseEntity.status(401).body(resp);
            }

            String roleName = user.getRole() != null ? user.getRole().name() : "ROLE_USER";
            String token = jwtUtils.generateJwtToken(email, roleName);

            resp.put("token", token);
            resp.put("role", roleName);
            resp.put("id", user.getIdUser());

            System.out.println("✅ Login correcto como USER");
            return ResponseEntity.ok(resp);
        }

        // 2️⃣ Buscar en COMPANIES si no está en USERS
        System.out.println("🔍 Usuario no está en USERS, buscando en COMPANIES…");

        Company company = companyRepository.findByEmailEntity_Email(email).orElse(null);

        if (company != null) {
            System.out.println("🔹 Empresa encontrada en COMPANIES");

            boolean matches = passwordEncoder.matches(password, company.getPassword());
            System.out.println("🔹 Coincide password empresa: " + matches);

            if (!matches) {
                resp.put("error", "Credenciales inválidas");
                return ResponseEntity.status(401).body(resp);
            }

            String token = jwtUtils.generateJwtToken(email, "COMPANY");

            resp.put("token", token);
            resp.put("role", "COMPANY");
            resp.put("id", company.getIdCompany());

            System.out.println("✅ Login correcto como COMPANY");
            return ResponseEntity.ok(resp);
        }

        // 3️⃣ No existe ni como usuario ni como empresa
        System.out.println("❌ No existe en USERS ni COMPANIES: " + email);

        resp.put("error", "Usuario no existe");
        return ResponseEntity.status(401).body(resp);
    }
}
