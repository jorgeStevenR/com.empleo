package com.portalempleos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.portalempleos.model.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    private String name;
    private String password;

    // ✅ Ahora es un Enum, no un String
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "cv_url")
    private String cvUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_company")
    @JsonIgnoreProperties({ "jobs", "emailEntity" })
    private Company company;

    @OneToOne
    @JoinColumn(name = "id_email", referencedColumnName = "idEmail", unique = true)
    @JsonIgnoreProperties("user")
    private Email emailEntity;

    // ===============================
    // ✅ Constructores
    // ===============================
    public User() {
    }

    public User(String name, String password, Role role, Email emailEntity) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.emailEntity = emailEntity;
        this.createdAt = LocalDateTime.now();
    }

    // ===============================
    // ✅ Getters y Setters
    // ===============================
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Email getEmailEntity() {
        return emailEntity;
    }

    public void setEmailEntity(Email emailEntity) {
        this.emailEntity = emailEntity;
    }

    // ===============================
    // ✅ toString (opcional, útil para logs)
    // ===============================
    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", email=" + (emailEntity != null ? emailEntity.getEmail() : "null") +
                '}';
    }
}
