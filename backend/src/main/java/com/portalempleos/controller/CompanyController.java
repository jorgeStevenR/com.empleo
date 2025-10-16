package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwt;

    public CompanyController(CompanyRepository companyRepository,
                             PasswordEncoder passwordEncoder,
                             JwtUtil jwt) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    private static String safe(Object o){ return o==null? "" : String.valueOf(o).trim(); }

    // ========== REGISTRO EMPRESA (PÚBLICO) ==========
    @PostMapping({"", "/register"})
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        String nit         = safe(body.get("nit"));
        String name        = safe(body.get("name"));
        String email       = safe(body.get("email")).toLowerCase();
        String website     = safe(body.get("website"));
        String location    = safe(body.get("location"));
        String description = safe(body.get("description"));
        String raw         = safe(body.get("password"));

        if (!StringUtils.hasText(nit) || !StringUtils.hasText(raw)) {
            return resp(false, "NIT y contraseña son obligatorios", null, HttpStatus.BAD_REQUEST);
        }
        if (companyRepository.findByNit(nit).isPresent()) {
            return resp(false, "La empresa ya existe (NIT)", null, HttpStatus.CONFLICT);
        }
        if (StringUtils.hasText(email) && companyRepository.findByEmail(email).isPresent()) {
            return resp(false, "La empresa ya existe (email)", null, HttpStatus.CONFLICT);
        }

        Company c = new Company();
        c.setNit(nit);
        c.setName(name);
        c.setEmail(StringUtils.hasText(email) ? email : null);
        c.setWebsite(website);
        c.setLocation(location);
        c.setDescription(description);
        c.setPassword(passwordEncoder.encode(raw));   // HASH AQUÍ

        Company saved = companyRepository.save(c);

        Map<String, Object> dto = new HashMap<>();
        dto.put("id", saved.getIdCompany());
        dto.put("nit", saved.getNit());
        dto.put("name", saved.getName());
        dto.put("email", saved.getEmail());

        return resp(true, "Empresa creada", dto, HttpStatus.CREATED);
    }

    // ========== LOGIN EMPRESA (PÚBLICO) ==========
    // Permite login usando NIT **o** email + password. No crea usuarios.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body){
        String byUsername = safe(body.get("username"));
        String fromNit    = safe(body.get("nit"));
        String fromEmail  = safe(body.get("email")).toLowerCase();
        String raw        = safe(body.get("password"));

        String userOrNit = StringUtils.hasText(byUsername)
                ? byUsername
                : (StringUtils.hasText(fromNit) ? fromNit : fromEmail);

        if (!StringUtils.hasText(userOrNit) || !StringUtils.hasText(raw)) {
            return resp(false, "Usuario (NIT o email) y contraseña son obligatorios", null, HttpStatus.BAD_REQUEST);
        }

        // sin lambda para evitar el “no efectivamente final”
        Company c = companyRepository.findByNit(userOrNit).orElse(null);
        if (c == null) c = companyRepository.findByEmail(userOrNit.toLowerCase()).orElse(null);
        if (c == null) {
            return resp(false, "Credenciales inválidas", null, HttpStatus.UNAUTHORIZED);
        }
        if (!passwordEncoder.matches(raw, c.getPassword())) {
            return resp(false, "Credenciales inválidas", null, HttpStatus.UNAUTHORIZED);
        }

        String token = jwt.generateToken(
                "COMPANY:"+c.getNit(),
                "EMPLOYER",
                Map.of("cid", c.getIdCompany(), "nit", c.getNit())
        );

        Map<String, Object> companyDto = new HashMap<>();
        companyDto.put("id",    c.getIdCompany());
        companyDto.put("nit",   c.getNit());
        companyDto.put("name",  c.getName());
        companyDto.put("email", c.getEmail());
        companyDto.put("role",  "EMPLOYER");

        Map<String, Object> res = new HashMap<>();
        res.put("ok", true);
        res.put("message", "Login ok");
        // mantenemos la clave 'user' para que tu frontend no cambie nada
        res.put("user", companyDto);
        res.put("token", token);
        return ResponseEntity.ok(res);
    }

    private static ResponseEntity<Map<String,Object>> resp(boolean ok, String msg, Object company, HttpStatus st){
        Map<String,Object> m = new HashMap<>();
        m.put("ok", ok);
        m.put("message", msg);
        if (company!=null) m.put("company", company);
        return ResponseEntity.status(st).body(m);
    }
}
