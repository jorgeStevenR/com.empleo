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

    // ================================================
    // 📌 SUBIR LOGO CON NOMBRE ÚNICO
    // ================================================
    public String uploadCompanyLogo(MultipartFile file, Long companyId) throws Exception {

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/png") ||
                  contentType.equals("image/jpeg") ||
                  contentType.equals("image/jpg") ||
                  contentType.equals("image/svg+xml"))) {

            throw new IllegalArgumentException("Formato inválido. Solo PNG, JPG, JPEG o SVG.");
        }

        // Extensión del archivo
        String ext = switch (contentType) {
            case "image/png" -> "png";
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/svg+xml" -> "svg";
            default -> "bin";
        };

        long timestamp = Instant.now().toEpochMilli();

        // clave en el bucket
        String key = "logos/" + companyId + "/" + timestamp + "." + ext;

        // encode para evitar errores con espacios
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putReq, RequestBody.fromBytes(file.getBytes()));

        // URL pública FINAL
        return publicUrl + "/" + bucket + "/" + encodedKey;
    }


    // ================================================
    // 📌 SUBIR CV (PDF)
    // ================================================
    public String uploadPdf(MultipartFile file, String folder) throws Exception {

        if (!file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Solo se permiten archivos PDF.");
        }

        String key = folder + "/" + Instant.now().toEpochMilli() + ".pdf";
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(putReq, RequestBody.fromBytes(file.getBytes()));

        return publicUrl + "/" + bucket + "/" + encodedKey;
    }
}
