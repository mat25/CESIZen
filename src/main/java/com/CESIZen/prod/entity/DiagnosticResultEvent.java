package com.CESIZen.prod.entity;

import jakarta.persistence.*;

@Entity
public class DiagnosticResultEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private DiagnosticEvent event;

    private int occurrences;

    @ManyToOne(optional = false)
    private DiagnosticResult result;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiagnosticEvent getEvent() {
        return event;
    }

    public void setEvent(DiagnosticEvent event) {
        this.event = event;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    public DiagnosticResult getResult() {
        return result;
    }

    public void setResult(DiagnosticResult result) {
        this.result = result;
    }
}