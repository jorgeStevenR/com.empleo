package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.model.Company;
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
            CompanyRepository companyRepository
    ) {
        this.supabaseS3Service = supabaseS3Service;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @PostMapping("/upload/logo/{companyId}")
    public ResponseEntity<?> uploadLogo(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file,
            Authentication auth
    ) {

        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
            }

            String requesterEmail = auth.getName().toLowerCase();

            Optional<Company> requesterCompanyOpt =
                    companyRepository.findByEmailEntity_Email(requesterEmail);

            Optional<User> requesterUserOpt =
                    userRepository.findByEmailEntity_Email(requesterEmail);

            Company target = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

            boolean isAdmin = requesterUserOpt.isPresent()
                    && requesterUserOpt.get().getRole().name().equals("ROLE_ADMIN");

            boolean isOwner = requesterCompanyOpt.isPresent()
                    && requesterCompanyOpt.get().getIdCompany().equals(companyId);

            if (!isAdmin && !isOwner) {
                return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
            }

            String url = supabaseS3Service.uploadCompanyLogo(file, companyId);

            target.setLogoUrl(url);
            companyRepository.save(target);

            return ResponseEntity.ok(Map.of(
                    "message", "Logo subido correctamente",
                    "logoUrl", url
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/upload/cv/{userId}")
    public ResponseEntity<?> uploadCv(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            Authentication auth
    ) {

        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
            }

            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
            }

            User user = userOpt.get();

            String requesterEmail = auth.getName().toLowerCase();
            if (!requesterEmail.equals(user.getEmailEntity().getEmail().toLowerCase())) {
                return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
            }

            String url = supabaseS3Service.uploadPdf(file, "cv/" + userId);

            user.setCvUrl(url);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "CV actualizado correctamente",
                    "cvUrl", url
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
