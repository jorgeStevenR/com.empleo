package com.portalempleos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 🌍 Configuración para servir archivos estáticos subidos (CVs, logos, etc.)
 *
 * Esta clase expone la carpeta /uploads como accesible vía /files/**,
 * permitiendo acceder a URLs como:
 * → http://localhost:8080/files/cv/mi_cv.pdf
 * → https://com-empleo.onrender.com/files/logos/empresa.png
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${public.base.url:http://localhost:8080}")
    private String publicBaseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String location = "file:" + uploadPath.toString() + "/";

        registry
                .addResourceHandler("/files/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);

        System.out.println("📁 Archivos estáticos servidos desde: " + location);
        System.out.println("🌐 URL pública base: " + publicBaseUrl);
    }
}
