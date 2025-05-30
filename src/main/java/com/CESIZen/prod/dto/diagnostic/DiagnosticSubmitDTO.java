package com.CESIZen.prod.dto.diagnostic;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class DiagnosticSubmitDTO {
    @NotNull(message = "La liste des événements ne peut pas être nulle")
    @NotEmpty(message = "La liste des événements ne peut pas être vide")
    private List<DiagnosticEventFrequency> selectedEvents;

    public static class DiagnosticEventFrequency {
        @NotNull(message = "L'ID de l'événement ne peut pas être nul")
        private Long eventId;
        @Min(value = 0, message = "Le nombre d'occurrences doit être supérieur ou égal à 0")
        private int occurrences;

        public Long getEventId() {
            return eventId;
        }

        public void setEventId(Long eventId) {
            this.eventId = eventId;
        }

        public int getOccurrences() {
            return occurrences;
        }

        public void setOccurrences(int occurrences) {
            this.occurrences = occurrences;
        }
    }

    public List<DiagnosticEventFrequency> getSelectedEvents() {
        return selectedEvents;
    }

    public void setSelectedEvents(List<DiagnosticEventFrequency> selectedEvents) {
        this.selectedEvents = selectedEvents;
    }
}

