package com.CESIZen.prod.entity;

import jakarta.persistence.*;

@Entity
public class DiagnosticScoreRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int minPoints;
    private int maxPoints;

    @Column(nullable = false)
    private String message;

    public DiagnosticScoreRange() {
    }

    public DiagnosticScoreRange(Long id, int minPoints, int maxPoints, String message) {
        this.id = id;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.message = message;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

