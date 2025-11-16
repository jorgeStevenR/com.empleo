package com.portalempleos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.Set;

@Service
public class FileStorageService {

    // ⚙️ Ahora usamos el mismo nombre de propiedad que en application.properties y .env
    @Value("${file.upload.dir:uploads}")
    private String baseDir;

    // ⚙️ También usamos la misma propiedad que en WebMvcConfig
    @Value("${public.base.url:http://localhost:8080}")
    private String publicBaseUrl;

    private void ensureDir(Path p) throws IOException {
        if (!Files.exists(p)) {
            Files.createDirectories(p);
        }
    }

    public String save(MultipartFile file,
                       String subfolder,
                       Set<String> allowedContentTypes,
                       String forcedExt) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IOException("Archivo vacío");
        }

        if (allowedContentTypes != null && !allowedContentTypes.isEmpty()) {
            String ct = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
            if (!allowedContentTypes.contains(ct)) {
                throw new IOException("Tipo de archivo no permitido: " + ct);
            }
        }

        Path folder = Paths.get(baseDir, subfolder).toAbsolutePath().normalize();
        ensureDir(folder);

        String extension = forcedExt != null ? forcedExt :
                StringUtils.getFilenameExtension(file.getOriginalFilename());

        String cleanExt = (extension == null || extension.isBlank())
                ? ""
                : ("." + extension.replace(".", ""));

        String filename = Instant.now().toEpochMilli() + cleanExt;

        Path target = folder.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        String cleanBaseUrl = publicBaseUrl.replaceAll("/+$", "");
        return String.format("%s/files/%s/%s", cleanBaseUrl, subfolder, filename);
    }
}
