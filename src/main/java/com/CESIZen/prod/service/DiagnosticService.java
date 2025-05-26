package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.diagnostic.DiagnosticEventDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticHistoryDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticResultDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticSubmitDTO;
import com.CESIZen.prod.entity.DiagnosticEvent;
import com.CESIZen.prod.entity.DiagnosticResult;
import com.CESIZen.prod.entity.DiagnosticScoreRange;
import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.repository.DiagnosticEventRepository;
import com.CESIZen.prod.repository.DiagnosticResultRepository;
import com.CESIZen.prod.repository.DiagnosticScoreRangeRepository;
import com.CESIZen.prod.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticService {

    private final DiagnosticEventRepository eventRepo;
    private final DiagnosticResultRepository resultRepo;
    private final SecurityUtils securityUtils;
    private final DiagnosticScoreRangeRepository rangeRepo;

    public DiagnosticService(
            DiagnosticEventRepository eventRepo,
            DiagnosticResultRepository resultRepo,
            SecurityUtils securityUtils,
            DiagnosticScoreRangeRepository rangeRepo) {
        this.eventRepo = eventRepo;
        this.resultRepo = resultRepo;
        this.securityUtils = securityUtils;
        this.rangeRepo = rangeRepo;
    }

    public List<DiagnosticEventDTO> getAllEvents() {
        return eventRepo.findAll().stream()
                .map(DiagnosticEventDTO::new)
                .toList();
    }

    public DiagnosticResultDTO submitDiagnostic(DiagnosticSubmitDTO dto, Authentication authentication) {
        List<DiagnosticEvent> selectedEvents = eventRepo.findAllById(dto.getSelectedEventIds());

        int score = selectedEvents.stream()
                .mapToInt(DiagnosticEvent::getPoints)
                .sum();

        String message = rangeRepo.findFirstByMinPointsLessThanEqualAndMaxPointsGreaterThanEqual(score, score)
                .map(DiagnosticScoreRange::getMessage)
                .orElse("Aucun message de résultat configuré");

        if (authentication != null && authentication.isAuthenticated()) {
            User user = securityUtils.getCurrentUser(authentication);
            DiagnosticResult result = new DiagnosticResult();
            result.setUser(user);
            result.setScore(score);
            resultRepo.save(result);
        }

        return new DiagnosticResultDTO(score, message);
    }

    public DiagnosticEventDTO createEvent(DiagnosticEventDTO dto) {
        DiagnosticEvent event = new DiagnosticEvent();
        event.setLabel(dto.getLabel());
        event.setPoints(dto.getPoints());
        return new DiagnosticEventDTO(eventRepo.save(event));
    }

    public DiagnosticEventDTO updateEvent(Long id, DiagnosticEventDTO dto) {
        DiagnosticEvent event = eventRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));
        event.setLabel(dto.getLabel());
        event.setPoints(dto.getPoints());
        return new DiagnosticEventDTO(eventRepo.save(event));
    }

    public void deleteEvent(Long id) {
        eventRepo.deleteById(id);
    }

    public List<DiagnosticHistoryDTO> getUserHistory(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);
        return resultRepo.findAllByUserId(user.getId()).stream()
                .map(DiagnosticHistoryDTO::new)
                .toList();
    }
}
