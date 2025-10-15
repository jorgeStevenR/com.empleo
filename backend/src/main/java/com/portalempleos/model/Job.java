package com.portalempleos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_job")
    private Long id_job;

    private String title;

    // LEGACY: nombre de empresa en texto (se mantiene para compatibilidad)
    @Column(name = "company")
    private String company;

    private String description;
    private String location;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    // NUEVO: relación a companies.id_company
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_company")  // asegúrate de tener esta columna en la tabla
    private Company companyEntity;

    @PrePersist
    public void prePersist() {
        if (created_at == null) created_at = LocalDateTime.now();
    }

    // ===== Getters y Setters =====
    public Long getId_job() { return id_job; }
    public void setId_job(Long id_job) { this.id_job = id_job; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // Legacy texto
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    // Relación con Company
    public Company getCompanyEntity() { return companyEntity; }
    public void setCompanyEntity(Company companyEntity) { this.companyEntity = companyEntity; }

    // Conveniencia para el frontend: muestra siempre un nombre de empresa
    @Transient
    public String getCompanyDisplay() {
        if (companyEntity != null && companyEntity.getName() != null && !companyEntity.getName().isBlank()) {
            return companyEntity.getName();
        }
        return company; // fallback legacy
    }
}
