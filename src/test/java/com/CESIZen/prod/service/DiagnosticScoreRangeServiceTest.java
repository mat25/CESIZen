package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticScoreRangeDTO;
import com.CESIZen.prod.entity.DiagnosticScoreRange;
import com.CESIZen.prod.exception.BadRequestException;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.DiagnosticScoreRangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiagnosticScoreRangeServiceTest {

    @Mock
    private DiagnosticScoreRangeRepository repository;

    @InjectMocks
    private DiagnosticScoreRangeService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMessageForScore_shouldReturnCorrectMessage() {
        DiagnosticScoreRange range = new DiagnosticScoreRange(1L, 0, 10, "Faible");
        when(repository.findAll()).thenReturn(List.of(range));

        String result = service.getMessageForScore(5);

        assertEquals("Faible", result);
    }

    @Test
    void getMessageForScore_shouldReturnDefaultMessageIfNoMatch() {
        when(repository.findAll()).thenReturn(List.of());

        String result = service.getMessageForScore(20);

        assertEquals("Niveau inconnu", result);
    }

    @Test
    void getAll_shouldReturnDTOList() {
        DiagnosticScoreRange range = new DiagnosticScoreRange(1L, 0, 10, "Faible");
        when(repository.findAll()).thenReturn(List.of(range));

        List<DiagnosticScoreRangeDTO> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Faible", result.get(0).getMessage());
    }

    @Test
    void create_shouldSaveNewRange() {
        DiagnosticScoreRangeDTO dto = new DiagnosticScoreRangeDTO();
        dto.setMinPoints(0);
        dto.setMaxPoints(10);
        dto.setMessage("Faible");

        DiagnosticScoreRange savedEntity = new DiagnosticScoreRange(1L, 0, 10, "Faible");
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any())).thenReturn(savedEntity);

        DiagnosticScoreRangeDTO result = service.create(dto);

        assertNotNull(result);
        assertEquals("Faible", result.getMessage());
    }

    @Test
    void create_shouldThrowBadRequestIfOverlapping() {
        DiagnosticScoreRangeDTO dto = new DiagnosticScoreRangeDTO();
        dto.setMinPoints(5);
        dto.setMaxPoints(15);
        dto.setMessage("Modéré");

        DiagnosticScoreRange existing = new DiagnosticScoreRange(1L, 0, 10, "Faible");
        when(repository.findAll()).thenReturn(List.of(existing));

        assertThrows(BadRequestException.class, () -> service.create(dto));
    }

    @Test
    void update_shouldUpdateExistingRange() {
        Long id = 1L;
        DiagnosticScoreRangeDTO dto = new DiagnosticScoreRangeDTO();
        dto.setMinPoints(10);
        dto.setMaxPoints(20);
        dto.setMessage("Moyen");

        DiagnosticScoreRange existing = new DiagnosticScoreRange(id, 0, 10, "Faible");
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.findAll()).thenReturn(List.of(existing));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        DiagnosticScoreRangeDTO result = service.update(id, dto);

        assertEquals("Moyen", result.getMessage());
        assertEquals(10, result.getMinPoints());
        assertEquals(20, result.getMaxPoints());
    }

    @Test
    void update_shouldThrowNotFoundIfIdDoesNotExist() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        DiagnosticScoreRangeDTO dto = new DiagnosticScoreRangeDTO();
        dto.setMinPoints(10);
        dto.setMaxPoints(20);
        dto.setMessage("Moyen");

        assertThrows(NotFoundException.class, () -> service.update(999L, dto));
    }

    @Test
    void update_shouldThrowBadRequestIfOverlapping() {
        Long id = 1L;
        DiagnosticScoreRange existing = new DiagnosticScoreRange(id, 0, 10, "Faible");
        DiagnosticScoreRange other = new DiagnosticScoreRange(2L, 15, 25, "Élevé");

        DiagnosticScoreRangeDTO dto = new DiagnosticScoreRangeDTO();
        dto.setMinPoints(20);
        dto.setMaxPoints(30);
        dto.setMessage("Moyen");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.findAll()).thenReturn(List.of(existing, other));

        assertThrows(BadRequestException.class, () -> service.update(id, dto));
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        Long id = 1L;

        MessageDTO result = service.delete(id);

        verify(repository, times(1)).deleteById(id);
        assertEquals("Plage supprimée", result.getMessage());
    }
}
