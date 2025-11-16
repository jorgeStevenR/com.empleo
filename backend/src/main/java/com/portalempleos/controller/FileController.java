package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.model.Company;
import com.portalempleos.model.enums.Role;
import com.portalempleos.repository.UserRepository;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.service.SupabaseS3Service;
import com.portalempleos.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final SupabaseS3Service supabaseS3Service;  
    private final FileStorageService fileStorageService; 
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public FileController(
            SupabaseS3Service supabaseS3Service,
            FileStorageService fileStorageService,
            UserRepository userRepository,
            CompanyRepository companyRepository) {

        this.supabaseS3Service = supabaseS3Service;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    // ===================================================
    // 📌 SUBIR CV (Solo dueño o ADMIN)
    // ===================================================
    @PostMapping("/upload/cv/{userId}")
    public ResponseEntity<?> uploadCv(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            Authentication auth) {

        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
            }

            String requesterEmail = auth.getName().toLowerCase();
            Optional<User> requesterOpt = userRepository.findByEmailEntity_Email(requesterEmail);

            if (requesterOpt.isEmpty()) {
                return ResponseEntity.status(403).body(Map.of("error", "Usuario no encontrado en token"));
            }

            User requester = requesterOpt.get();
            boolean isAdmin = requester.getRole() == Role.ROLE_ADMIN;

            User target = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // SOLO ADMIN o EL MISMO usuario
            if (!isAdmin && !target.getEmailEntity().getEmail().equalsIgnoreCase(requesterEmail)) {
                return ResponseEntity.status(403).body(Map.of("error",
                        "No tienes permisos para modificar este perfil"));
            }

            // Subir archivo a Supabase Storage
            String url = supabaseS3Service.uploadPdf(file, "cv");

            target.setCvUrl(url);
            userRepository.save(target);

            return ResponseEntity.ok(Map.of(
                    "message", "CV subido correctamente.",
                    "cvUrl", url
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error subiendo CV: " + e.getMessage()));
        }
    }

    // ===================================================
    // 🔵 SUBIR LOGO (solo empresa en su propia cuenta)
    // ===================================================
    @PostMapping("/upload/logo/{companyId}")
    public ResponseEntity<?> uploadLogo(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file,
            Authentication auth) {

        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
            }

            String requesterEmail = auth.getName().toLowerCase();
            Optional<Company> requesterCompanyOpt =
                    companyRepository.findByEmailEntity_Email(requesterEmail);
            Optional<User> requesterUserOpt =
                    userRepository.findByEmailEntity_Email(requesterEmail);

            boolean isAdmin = requesterUserOpt.isPresent() &&
                    requesterUserOpt.get().getRole() == Role.ROLE_ADMIN;

            Company target = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

            // Solo su propio logo si no es administrador
            if (!isAdmin && !target.getEmailEntity().getEmail().equalsIgnoreCase(requesterEmail)) {
                return ResponseEntity.status(403).body(Map.of("error",
                        "No tienes permisos para modificar esta empresa"));
            }

            Set<String> allowedTypes = Set.of("image/png", "image/jpeg", "image/jpg", "image/svg+xml");
            String url = fileStorageService.save(file, "logos", allowedTypes, null);

            target.setLogoUrl(url);
            companyRepository.save(target);

            return ResponseEntity.ok(Map.of(
                    "message", "Logo actualizado correctamente.",
                    "logoUrl", url
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al subir logo: " + e.getMessage()));
        }
    }
}
