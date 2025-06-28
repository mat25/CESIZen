package com.CESIZen.prod.dto.diagnostic;

import com.CESIZen.prod.entity.DiagnosticResult;

import java.time.format.DateTimeFormatter;

public class DiagnosticExportDTO {
    private int score;
    private String submittedAt;

    public DiagnosticExportDTO(DiagnosticResult diagnostic) {
        this.score = diagnostic.getScore();
        this.submittedAt = diagnostic.getSubmittedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }
}