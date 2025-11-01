package com.portalempleos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private String password;

    @Column(length = 20, nullable = false)
    private String role; // USER | EMPLOYER | ADMIN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_company")
    private Company companyEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_email", unique = true)
    private Email emailEntity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
        if (role == null || role.isBlank())
            role = "USER";
    }

    // ===== Getters & Setters =====
    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Company getCompanyEntity() {
        return companyEntity;
    }

    public void setCompanyEntity(Company companyEntity) {
        this.companyEntity = companyEntity;
    }

    public Email getEmailEntity() {
        return emailEntity;
    }

    public void setEmailEntity(Email emailEntity) {
        this.emailEntity = emailEntity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
