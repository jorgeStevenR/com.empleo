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

    /**
     * 📂 Directorio base donde se guardan los archivos subidos
     * (se puede configurar en application.properties con "file.upload.dir")
     */
    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    /**
     * 🌐 URL base pública de tu backend, usada para construir las rutas absolutas
     * (por ejemplo, https://com-empleo.onrender.com)
     */
    @Value("${public.base.url:http://localhost:8080}")
    private String publicBaseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ruta física del directorio "uploads"
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // Prefijo "file:" indica a Spring que use archivos del sistema
        String location = "file:" + uploadPath.toString() + "/";

        // Expone la ruta /files/** hacia los archivos dentro de /uploads/
        registry
                .addResourceHandler("/files/**")
                .addResourceLocations(location)
                .setCachePeriod(3600); // cache de 1 hora (mejora rendimiento)

        System.out.println("📁 Archivos estáticos servidos desde: " + location);
        System.out.println("🌐 URL pública base: " + publicBaseUrl);
    }
}
