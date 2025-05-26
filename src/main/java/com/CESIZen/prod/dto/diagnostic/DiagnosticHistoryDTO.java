package com.CESIZen.prod.dto.diagnostic;

import com.CESIZen.prod.entity.DiagnosticResult;

import java.time.LocalDateTime;

public class DiagnosticHistoryDTO {
    private int score;
    private String level;
    private LocalDateTime submittedAt;

    public DiagnosticHistoryDTO(DiagnosticResult result) {
        this.score = result.getScore();
        this.level = computeLevel(result.getScore());
        this.submittedAt = result.getSubmittedAt();
    }

    private String computeLevel(int score) {
        if (score < 150) return "Faible";
        if (score < 300) return "Modéré";
        return "Élevé";
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}

