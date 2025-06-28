package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.diagnostic.*;
import com.CESIZen.prod.entity.*;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.DiagnosticEventRepository;
import com.CESIZen.prod.repository.DiagnosticResultRepository;
import com.CESIZen.prod.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiagnosticServiceTest {

    private DiagnosticEventRepository eventRepo;
    private DiagnosticResultRepository resultRepo;
    private SecurityUtils securityUtils;
    private DiagnosticScoreRangeService rangeService;
    private DiagnosticService diagnosticService;

    @BeforeEach
    void setUp() {
        eventRepo = mock(DiagnosticEventRepository.class);
        resultRepo = mock(DiagnosticResultRepository.class);
        securityUtils = mock(SecurityUtils.class);
        rangeService = mock(DiagnosticScoreRangeService.class);
        diagnosticService = new DiagnosticService(eventRepo, resultRepo, securityUtils, rangeService);
    }

    @Test
    void getAllEvents_shouldReturnListOfDTOs() {
        DiagnosticEvent event = new DiagnosticEvent();
        event.setId(1L);
        event.setLabel("Fatigue");
        event.setPoints(2);
        when(eventRepo.findAllByDeletedFalse()).thenReturn(List.of(event));

        List<DiagnosticEventDTO> result = diagnosticService.getAllEvents();

        assertEquals(1, result.size());
        assertEquals("Fatigue", result.get(0).getLabel());
        assertEquals(2, result.get(0).getPoints());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void submitDiagnostic_anonymousUser_shouldReturnResult() {
        DiagnosticEvent event = new DiagnosticEvent();
        event.setId(1L);
        event.setPoints(3);

        // Remplacement SelectedEventDTO -> DiagnosticEventFrequency
        DiagnosticSubmitDTO.DiagnosticEventFrequency freq = new DiagnosticSubmitDTO.DiagnosticEventFrequency();
        freq.setEventId(1L);
        freq.setOccurrences(2);

        DiagnosticSubmitDTO dto = new DiagnosticSubmitDTO();
        dto.setSelectedEvents(List.of(freq));

        when(eventRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(event));
        when(rangeService.getMessageForScore(6)).thenReturn("Message");

        DiagnosticResultDTO result = diagnosticService.submitDiagnostic(dto, null);

        assertEquals(6, result.getScore());
        assertEquals("Message", result.getLevel());
    }

    @Test
    void submitDiagnostic_authenticatedUser_shouldSaveResult() {
        DiagnosticEvent event = new DiagnosticEvent();
        event.setId(1L);
        event.setPoints(5);

        DiagnosticSubmitDTO.DiagnosticEventFrequency freq = new DiagnosticSubmitDTO.DiagnosticEventFrequency();
        freq.setEventId(1L);
        freq.setOccurrences(1);

        DiagnosticSubmitDTO dto = new DiagnosticSubmitDTO();
        dto.setSelectedEvents(List.of(freq));

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);

        User user = new User();
        user.setId(42L);
        user.setUsername("test-user");
        when(securityUtils.getCurrentUser(auth)).thenReturn(user);

        when(eventRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(event));
        when(rangeService.getMessageForScore(5)).thenReturn("Score normal");

        DiagnosticResultDTO result = diagnosticService.submitDiagnostic(dto, auth);

        assertEquals(5, result.getScore());
        assertEquals("Score normal", result.getLevel());
        verify(resultRepo).save(any(DiagnosticResult.class));
    }

    @Test
    void submitDiagnostic_shouldThrowIfEventNotFound() {
        DiagnosticSubmitDTO.DiagnosticEventFrequency freq = new DiagnosticSubmitDTO.DiagnosticEventFrequency();
        freq.setEventId(99L);
        freq.setOccurrences(1);

        DiagnosticSubmitDTO dto = new DiagnosticSubmitDTO();
        dto.setSelectedEvents(List.of(freq));

        when(eventRepo.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                diagnosticService.submitDiagnostic(dto, null));
        assertEquals("Événement introuvable", ex.getMessage());
    }

    @Test
    void createEvent_shouldSaveAndReturnDTO() {
        DiagnosticEventDTO dto = new DiagnosticEventDTO();
        dto.setId(null);
        dto.setLabel("Stress");
        dto.setPoints(4);

        DiagnosticEvent saved = new DiagnosticEvent();
        saved.setId(10L);
        saved.setLabel("Stress");
        saved.setPoints(4);

        when(eventRepo.save(any(DiagnosticEvent.class))).thenReturn(saved);

        DiagnosticEventDTO result = diagnosticService.createEvent(dto);

        assertEquals("Stress", result.getLabel());
        assertEquals(4, result.getPoints());
        assertEquals(10L, result.getId());
    }

    @Test
    void updateEvent_shouldModifyEvent() {
        DiagnosticEvent existing = new DiagnosticEvent();
        existing.setId(1L);
        existing.setLabel("Old");
        existing.setPoints(1);

        DiagnosticEventDTO update = new DiagnosticEventDTO();
        update.setId(1L);
        update.setLabel("New");
        update.setPoints(5);

        when(eventRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existing));
        when(eventRepo.save(any(DiagnosticEvent.class))).thenReturn(existing);

        DiagnosticEventDTO result = diagnosticService.updateEvent(1L, update);

        assertEquals("New", result.getLabel());
        assertEquals(5, result.getPoints());
        assertEquals(1L, result.getId());
    }

    @Test
    void updateEvent_shouldThrowIfNotFound() {
        when(eventRepo.findByIdAndDeletedFalse(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                diagnosticService.updateEvent(2L, new DiagnosticEventDTO()));
        assertEquals("Événement introuvable", ex.getMessage());
    }

    @Test
    void deleteEvent_shouldSetDeletedTrue() {
        DiagnosticEvent event = new DiagnosticEvent();
        event.setId(1L);
        event.setDeleted(false);

        when(eventRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(event));

        MessageDTO result = diagnosticService.deleteEvent(1L);

        assertTrue(event.isDeleted());
        verify(eventRepo).save(event);
        assertEquals("Événement supprimé", result.getMessage());
    }

    @Test
    void deleteEvent_shouldThrowIfNotFound() {
        when(eventRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> diagnosticService.deleteEvent(1L));
    }

    @Test
    void getUserHistory_shouldReturnUserResults() {
        Authentication auth = mock(Authentication.class);
        User user = new User();
        user.setId(7L);
        user.setUsername("history-user");

        DiagnosticResult result = new DiagnosticResult();
        result.setScore(10);
        result.setUser(user);
        result.setId(88L);
        result.setEventDetails(List.of());

        when(securityUtils.getCurrentUser(auth)).thenReturn(user);
        when(resultRepo.findAllByUserId(7L)).thenReturn(List.of(result));
        when(rangeService.getMessageForScore(10)).thenReturn("OK");

        List<DiagnosticHistoryDTO> history = diagnosticService.getUserHistory(auth);

        assertEquals(1, history.size());
        assertEquals(10, history.get(0).getScore());
        assertEquals("OK", history.get(0).getLevel());
        assertEquals(88L, history.get(0).getId());
    }
}
