package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.service.CompanyService;
import com.portalempleos.service.EmailService;
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

    private final CompanyService companyService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwt;

    public CompanyController(CompanyService companyService,
                             EmailService emailService,
                             PasswordEncoder passwordEncoder,
                             JwtUtil jwt) {
        this.companyService = companyService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    private static String safe(Object o){ return o==null? "" : String.valueOf(o).trim(); }

    // ========== REGISTER COMPANY ==========
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
            return resp(false, "NIT and password are required", null, HttpStatus.BAD_REQUEST);
        }

        if (companyService.getByNit(nit).isPresent()) {
            return resp(false, "Company already exists (NIT)", null, HttpStatus.CONFLICT);
        }

        if (emailService.findByEmail(email).isPresent()) {
            return resp(false, "Email already registered", null, HttpStatus.CONFLICT);
        }

        Company c = new Company();
        c.setNit(nit);
        c.setName(name);
        c.setWebsite(website);
        c.setLocation(location);
        c.setDescription(description);
        c.setPassword(raw);

        Company saved = companyService.register(c, email);

        Map<String, Object> dto = new HashMap<>();
        dto.put("id", saved.getIdCompany());
        dto.put("nit", saved.getNit());
        dto.put("name", saved.getName());
        dto.put("email", saved.getEmailEntity() != null ? saved.getEmailEntity().getEmail() : null);

        return resp(true, "Company registered successfully", dto, HttpStatus.CREATED);
    }

    // ========== LOGIN COMPANY ==========
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body){
        String username = safe(body.get("username"));
        String raw = safe(body.get("password"));

        var companyOpt = companyService.getByNit(username);
        if (companyOpt.isEmpty()) {
            companyOpt = companyService.getByEmail(username);
        }

        if (companyOpt.isEmpty()) {
            return resp(false, "Invalid credentials", null, HttpStatus.UNAUTHORIZED);
        }

        Company c = companyOpt.get();
        if (!passwordEncoder.matches(raw, c.getPassword())) {
            return resp(false, "Invalid credentials", null, HttpStatus.UNAUTHORIZED);
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
        companyDto.put("email", c.getEmailEntity() != null ? c.getEmailEntity().getEmail() : null);
        companyDto.put("role",  "EMPLOYER");

        Map<String, Object> res = new HashMap<>();
        res.put("ok", true);
        res.put("message", "Login ok");
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
