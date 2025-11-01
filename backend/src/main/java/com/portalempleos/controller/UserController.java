package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.service.EmailService;
import com.portalempleos.service.UserService;
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

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService,
                          EmailService emailService,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ======= REGISTER USER =======
    @PostMapping({"", "/register"})
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        String name = safe(body.get("name"));
        String email = safe(body.get("email")).toLowerCase();
        String rawPassword = safe(body.get("password"));
        String role = StringUtils.hasText(safe(body.get("role"))) ? safe(body.get("role")).toUpperCase() : "USER";

        if (!StringUtils.hasText(email) || !StringUtils.hasText(rawPassword)) {
            return build(false, "Email and password are required", null, null, HttpStatus.BAD_REQUEST);
        }

        if (emailService.findByEmail(email).isPresent()) {
            return build(false, "Email already exists", null, null, HttpStatus.CONFLICT);
        }

        User u = new User();
        u.setName(name);
        u.setPassword(rawPassword);
        u.setRole(role);

        User saved = userService.registerUser(u, email);
        Map<String, Object> userDto = sanitizeUser(saved);

        String token = jwtUtil.generateToken(email, saved.getRole(), Map.of("uid", saved.getIdUser()));

        return build(true, "User registered successfully", userDto, token, HttpStatus.CREATED);
    }

    // ======= LOGIN =======
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        String email = safe(body.get("email")).toLowerCase();
        String raw = safe(body.get("password"));

        var opt = userService.findByEmail(email);
        if (opt.isEmpty()) return build(false, "Invalid credentials", null, null, HttpStatus.UNAUTHORIZED);

        User u = opt.get();
        if (!passwordEncoder.matches(raw, u.getPassword())) {
            return build(false, "Invalid credentials", null, null, HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(email, u.getRole(), Map.of("uid", u.getIdUser()));
        return build(true, "Login successful", sanitizeUser(u), token, HttpStatus.OK);
    }

    // ===== Helpers =====
    private static String safe(Object o) { return o == null ? "" : String.valueOf(o).trim(); }

    private static Map<String, Object> sanitizeUser(User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getIdUser());
        m.put("name", u.getName());
        m.put("email", u.getEmailEntity() != null ? u.getEmailEntity().getEmail() : null);
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
