package com.portalempleos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    private String name;

    @Column(nullable = false)
    private String email;

    // Nota: en prod deberías hashear esta contraseña
    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // USER | EMPLOYER | ADMIN
    @Column(name = "role")
    private String role = "USER";

    // Relación opcional: a qué empresa pertenece el usuario (empleador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_company")
    private Company companyEntity;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (role == null || role.isBlank()) role = "USER";
    }

    // ===== Getters & Setters =====
    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Company getCompanyEntity() { return companyEntity; }
    public void setCompanyEntity(Company companyEntity) { this.companyEntity = companyEntity; }
}
