package com.portalempleos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_company")
    private Long idCompany;

    @Column(nullable = false, unique = true)
    private String nit;

    private String name;

    @Column(unique = true)               // <-- ahora existe email en la entidad
    private String email;

    private String website;
    private String location;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // getters/setters
    public Long getIdCompany() { return idCompany; }
    public void setIdCompany(Long idCompany) { this.idCompany = idCompany; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
