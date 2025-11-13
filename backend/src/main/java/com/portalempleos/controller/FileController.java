package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.model.Company;
import com.portalempleos.repository.UserRepository;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 📁 Controlador para subir archivos (CVs o logos)
 * Guarda el archivo en disco y actualiza automáticamente
 * el campo cvUrl o logoUrl en la base de datos correspondiente.
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public FileController(FileStorageService fileStorageService,
            UserRepository userRepository,
            CompanyRepository companyRepository) {
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * 📤 Subir CV (PDF) para un usuario candidato.
     * Guarda el archivo y actualiza automáticamente el campo cvUrl.
     */
    @PostMapping("/upload/cv/{userId}")
    public ResponseEntity<?> uploadCv(@PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            Set<String> allowedTypes = Set.of("application/pdf");
            String url = fileStorageService.save(file, "cv", allowedTypes, "pdf");

            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
            }

            User user = optUser.get();
            user.setCvUrl(url);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "✅ CV subido y actualizado correctamente.",
                    "cvUrl", url));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al subir CV: " + e.getMessage()));
        }
    }

    /**
     * 🏢 Subir logo (imagen) para una empresa.
     * Guarda el archivo y actualiza automáticamente el campo logoUrl.
     */
    @PostMapping("/upload/logo/{companyId}")
    public ResponseEntity<?> uploadLogo(@PathVariable Long companyId,
            @RequestParam("file") MultipartFile file) {
        try {
            Set<String> allowedTypes = Set.of("image/png", "image/jpeg", "image/jpg", "image/svg+xml");
            String url = fileStorageService.save(file, "logos", allowedTypes, null);

            Optional<Company> optCompany = companyRepository.findById(companyId);
            if (optCompany.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Empresa no encontrada"));
            }

            Company company = optCompany.get();
            company.setLogoUrl(url);
            companyRepository.save(company);

            return ResponseEntity.ok(Map.of(
                    "message", "✅ Logo subido y actualizado correctamente.",
                    "logoUrl", url));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al subir logo: " + e.getMessage()));
        }
    }
}
