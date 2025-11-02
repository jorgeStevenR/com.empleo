package com.portalempleos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idApplication;

    @ManyToOne
    @JoinColumn(name = "id_user")
    @JsonIgnoreProperties({"company", "emailEntity", "password", "role"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_job")
    @JsonIgnoreProperties({"applications", "company"})
    private Job job;

    @Column(name = "cover_letter")
    private String coverLetter;

    @Column(name = "url_img")
    private String urlImg;

    private String status;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt = LocalDateTime.now();

    // Getters y setters
    public Long getIdApplication() {
        return idApplication;
    }

    public void setIdApplication(Long idApplication) {
        this.idApplication = idApplication;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}
