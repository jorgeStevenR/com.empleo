package com.portalempleos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies", uniqueConstraints = {
        @UniqueConstraint(name = "ux_companies_nit", columnNames = "nit")
})
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_company")
    private Long idCompany;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nit; // NIT único

    private String website;
    private String location;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters & Setters
    public Long getIdCompany() { return idCompany; }
    public void setIdCompany(Long idCompany) { this.idCompany = idCompany; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
