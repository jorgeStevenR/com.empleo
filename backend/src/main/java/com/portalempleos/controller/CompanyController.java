package com.portalempleos.controller;

import com.portalempleos.model.Company;
import com.portalempleos.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    // Crear nueva empresa
    @PostMapping
    public ResponseEntity<?> register(@RequestBody Company company) {
        try {
            return ResponseEntity.ok(service.registerCompany(company));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar empresa
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Company updated) {
        Optional<Company> existing = service.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        Company company = existing.get();
        company.setName(updated.getName());
        company.setNit(updated.getNit());
        company.setWebsite(updated.getWebsite());
        company.setLocation(updated.getLocation());
        company.setDescription(updated.getDescription());
        company.setPassword(updated.getPassword());

        return ResponseEntity.ok(service.save(company));
    }

    // 🔹 Listar todas
    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // 🔹 Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.deleteById(id)
                ? ResponseEntity.ok("Empresa eliminada correctamente")
                : ResponseEntity.notFound().build();
    }
}
