package com.CESIZen.prod.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class DiagnosticResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;

    private LocalDateTime submittedAt = LocalDateTime.now();

    @ManyToOne(optional = false)
    private User user;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL)
    private List<DiagnosticResultEvent> eventDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<DiagnosticResultEvent> getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(List<DiagnosticResultEvent> eventDetails) {
        this.eventDetails = eventDetails;
    }
}
