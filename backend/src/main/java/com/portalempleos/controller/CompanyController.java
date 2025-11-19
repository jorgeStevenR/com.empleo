package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.service.CompanyService;
import com.portalempleos.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService service;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorage;

    public CompanyController(CompanyService service,
                             PasswordEncoder passwordEncoder,
                             FileStorageService fileStorage) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.fileStorage = fileStorage;
    }

    // ✔ REGISTRO DE EMPRESA CON VALIDACIONES
    @PostMapping
    public ResponseEntity<?> register(@RequestBody Company company) {
        try {
            return ResponseEntity.ok(service.registerCompany(company));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Company updated) {
        Optional<Company> existingOpt = service.findById(id);
        if (existingOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Company existing = existingOpt.get();
        existing.setName(updated.getName());
        existing.setNit(updated.getNit());
        existing.setWebsite(updated.getWebsite());
        existing.setLocation(updated.getLocation());
        existing.setDescription(updated.getDescription());

        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        }

        return ResponseEntity.ok(service.save(existing));
    }

    @PutMapping(path = "/{id}/logo", consumes = { "multipart/form-data" })
    public ResponseEntity<?> uploadLogo(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        Optional<Company> opt = service.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        try {
            String url = fileStorage.save(file, "logos",
                    Set.of("image/png", "image/jpeg", "image/jpg", "image/svg+xml"), null);

            Company c = opt.get();
            c.setLogoUrl(url);
            return ResponseEntity.ok(service.save(c));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error subiendo logo: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.deleteById(id)
                ? ResponseEntity.ok("Empresa eliminada correctamente")
                : ResponseEntity.notFound().build();
    }
}
