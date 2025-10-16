package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.repository.UserRepository;
import com.portalempleos.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ======= REGISTRO USUARIO (PÚBLICO) =======
    @PostMapping({"", "/register"})
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        String name = safe(body.get("name"));
        String email = safe(body.get("email")).toLowerCase();
        String rawPassword = safe(body.get("password"));
        String role = StringUtils.hasText(safe(body.get("role"))) ? safe(body.get("role")).toUpperCase() : "USER";

        if (!StringUtils.hasText(email) || !StringUtils.hasText(rawPassword)) {
            return build(false, "Email y contraseña son obligatorios", null, null, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return build(false, "El email ya está registrado", null, null, HttpStatus.CONFLICT);
        }

        User u = new User();
        u.setName(name);
        u.setEmail(email);
        // HASH AQUÍ
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(role);

        User saved = userRepository.save(u);

        Map<String, Object> userDto = sanitizeUser(saved);
        // token opcional directo al registrar
        String token = jwtUtil.generateToken(
                saved.getEmail(),
                saved.getRole(),
                Map.of("uid", saved.getIdUser())
        );

        return build(true, "Usuario creado", userDto, token, HttpStatus.CREATED);
    }

    // ======= LOGIN (PÚBLICO) =======
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        String email = safe(body.get("email")).toLowerCase();
        String raw = safe(body.get("password"));

        var opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) {
            return build(false, "Credenciales inválidas", null, null, HttpStatus.UNAUTHORIZED);
        }
        User u = opt.get();
        if (!passwordEncoder.matches(raw, u.getPassword())) {
            return build(false, "Credenciales inválidas", null, null, HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(
                u.getEmail(),
                u.getRole(),
                Map.of("uid", u.getIdUser())
        );

        return build(true, "Login ok", sanitizeUser(u), token, HttpStatus.OK);
    }

    // ===== Helpers =====
    private static String safe(Object o) { return o == null ? "" : String.valueOf(o).trim(); }

    private static Map<String, Object> sanitizeUser(User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getIdUser());
        m.put("idUser", u.getIdUser()); // compat
        m.put("name", u.getName());
        m.put("email", u.getEmail());
        m.put("role", u.getRole());
        return m;
    }

    private static ResponseEntity<Map<String, Object>> build(boolean ok, String msg, Object user, String token, HttpStatus status) {
        Map<String, Object> res = new HashMap<>();
        res.put("ok", ok);
        res.put("message", msg);
        if (user != null) res.put("user", user);
        if (token != null) res.put("token", token);
        return ResponseEntity.status(status).body(res);
    }
}
