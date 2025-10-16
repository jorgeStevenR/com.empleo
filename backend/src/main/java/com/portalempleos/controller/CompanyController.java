package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.repository.CompanyRepository;
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

    public CompanyController(CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping({"", "/register"})
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        String nit = safe(body.get("nit"));
        String name = safe(body.get("name"));
        String raw = safe(body.get("password"));

        if (!StringUtils.hasText(nit) || !StringUtils.hasText(raw)) {
            return resp(false, "NIT y contraseña son obligatorios", null, HttpStatus.BAD_REQUEST);
        }
        if (companyRepository.findByNit(nit).isPresent()) {
            return resp(false, "La empresa ya existe", null, HttpStatus.CONFLICT);
        }

        Company c = new Company();
        c.setNit(nit);
        c.setName(name);
        // HASH AQUÍ
        c.setPassword(passwordEncoder.encode(raw));

        Company saved = companyRepository.save(c);
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", saved.getIdCompany());
        dto.put("nit", saved.getNit());
        dto.put("name", saved.getName());

        return resp(true, "Empresa creada", dto, HttpStatus.CREATED);
    }

    private static String safe(Object o){ return o==null? "": String.valueOf(o).trim(); }

    private static ResponseEntity<Map<String,Object>> resp(boolean ok, String msg, Object company, HttpStatus st){
        Map<String,Object> m = new HashMap<>();
        m.put("ok", ok);
        m.put("message", msg);
        if (company!=null) m.put("company", company);
        return ResponseEntity.status(st).body(m);
    }
}
