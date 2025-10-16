package com.portalempleos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_application")
    private Long idApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_job", nullable = false)
    private Job job;

    @Column(name = "cover_letter")
    private String coverLetter;

    @Column(name = "url_img")
    private String urlImg;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    public enum Status { Pendiente, Revisando, Rechazado, Aceptado }

    @PrePersist
    public void prePersist() {
        if (appliedAt == null) appliedAt = LocalDateTime.now();
        if (status == null) status = Status.Pendiente;
    }

    // Getters/Setters
    public Long getIdApplication() { return idApplication; }
    public void setIdApplication(Long idApplication) { this.idApplication = idApplication; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public String getUrlImg() { return urlImg; }
    public void setUrlImg(String urlImg) { this.urlImg = urlImg; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}
