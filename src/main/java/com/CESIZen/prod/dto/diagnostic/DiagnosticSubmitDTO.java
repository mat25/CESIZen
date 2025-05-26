package com.CESIZen.prod.dto.diagnostic;

import java.util.List;

public class DiagnosticSubmitDTO {
    private List<DiagnosticEventFrequency> selectedEvents;

    public static class DiagnosticEventFrequency {
        private Long eventId;
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

