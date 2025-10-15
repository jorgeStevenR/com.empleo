package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.model.User;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    private final CompanyRepository companyRepository;

    public UserController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    private Map<String, Object> ok(Object user, String token) {
        Map<String, Object> m = new HashMap<>();
        m.put("ok", true);
        m.put("user", user);
        m.put("token", token);
        return m;
    }

    private Map<String, Object> err(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("ok", false);
        m.put("message", msg);
        return m;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Registro:
     * {
     *   "name":"...", "email":"...", "password":"...",
     *   "role":"USER|EMPLOYER|ADMIN",
     *   "companyNit":"901234567-8"   // preferido
     *   // o "companyId": 1          // compatible
     * }
     */
    @PostMapping("/register")
    public Object registerUser(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            String role = (String) body.getOrDefault("role", "USER");

            if (email == null || password == null) return err("Email y password son obligatorios");

            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPassword(password); // TODO: hashear en prod
            u.setRole(role == null || role.isBlank() ? "USER" : role);

            // Preferir NIT
            Object nitObj = body.get("companyNit");
            if (nitObj instanceof String nit && !nit.isBlank()) {
                companyRepository.findByNit(nit).ifPresent(u::setCompanyEntity);
            } else {
                Object companyIdObj = body.get("companyId");
                if (companyIdObj instanceof Number num) {
                    companyRepository.findById(num.longValue()).ifPresent(u::setCompanyEntity);
                }
            }

            User saved = userService.registerUser(u);
            return ok(saved, null);
        } catch (Exception e) {
            return err("Error registrando: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public Object loginUser(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            User user = userService.getUserByEmail(email);
            if (user != null && user.getPassword().equals(password)) {
                return ok(user, null);
            }
            return err("Credenciales incorrectas");
        } catch (Exception e) {
            return err("Error en login: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ok(Map.of("deletedId", id), null);
    }
}
