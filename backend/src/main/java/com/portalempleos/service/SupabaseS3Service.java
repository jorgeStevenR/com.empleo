package com.portalempleos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
public class SupabaseS3Service {

    private final S3Client s3Client;

    @Value("${supabase.s3.bucket}")
    private String bucket;

    @Value("${supabase.s3.public-url}")
    private String publicUrl;

    public SupabaseS3Service(
            @Value("${supabase.s3.endpoint}") String endpoint,
            @Value("${supabase.s3.region}") String region,
            @Value("${supabase.s3.access-key}") String accessKey,
            @Value("${supabase.s3.secret-key}") String secretKey
    ) {

        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .endpointOverride(java.net.URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .forcePathStyle(true)
                .build();
    }

    // ================================
    // ✔ TEST DE CONEXIÓN
    // ================================
    public void testConnection() throws Exception {
        try {
            s3Client.listBuckets();
        } catch (Exception e) {
            throw new Exception("Error conectando a Supabase S3: " + e.getMessage(), e);
        }
    }

    // ================================
    // ✔ SUBIR PDF
    // ================================
    public String uploadPdf(MultipartFile file, String folder) throws Exception {

        if (!file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Solo se permiten archivos PDF.");
        }

        // Nombre interno del archivo
        String key = folder + "/" + Instant.now().toEpochMilli() + ".pdf";

        // Para evitar problemas de espacios y %
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);

        // Petición de subida
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(putReq, RequestBody.fromBytes(file.getBytes()));

        // 🔥 FIX COMPLETO: CONSTRUIR URL REAL DE SUPABASE STORAGE
        return publicUrl + "/" + bucket + "/" + encodedKey;
    }
}
