package com.portalempleos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCompany;

    private String nit;
    private String name;
    private String website;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String password;

    @Column(nullable = false)
    private String role = "COMPANY";

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "id_email", referencedColumnName = "idEmail", unique = true)
    @JsonIgnoreProperties({ "company" })
    private Email emailEntity;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Job> jobs;

    // Getters y Setters
    public Long getIdCompany() { return idCompany; }
    public void setIdCompany(Long idCompany) { this.idCompany = idCompany; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Email getEmailEntity() { return emailEntity; }
    public void setEmailEntity(Email emailEntity) { this.emailEntity = emailEntity; }

    public List<Job> getJobs() { return jobs; }
    public void setJobs(List<Job> jobs) { this.jobs = jobs; }
}
