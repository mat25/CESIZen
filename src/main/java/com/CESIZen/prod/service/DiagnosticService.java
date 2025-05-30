package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticEventDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticHistoryDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticResultDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticSubmitDTO;
import com.CESIZen.prod.entity.DiagnosticEvent;
import com.CESIZen.prod.entity.DiagnosticResult;
import com.CESIZen.prod.entity.DiagnosticResultEvent;
import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.DiagnosticEventRepository;
import com.CESIZen.prod.repository.DiagnosticResultRepository;
import com.CESIZen.prod.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticService {

    private final DiagnosticEventRepository eventRepo;
    private final DiagnosticResultRepository resultRepo;
    private final SecurityUtils securityUtils;
    private final DiagnosticScoreRangeService rangeService;

    public DiagnosticService(
            DiagnosticEventRepository eventRepo,
            DiagnosticResultRepository resultRepo,
            SecurityUtils securityUtils,
            DiagnosticScoreRangeService rangeService) {
        this.eventRepo = eventRepo;
        this.resultRepo = resultRepo;
        this.securityUtils = securityUtils;
        this.rangeService = rangeService;
    }

    public List<DiagnosticEventDTO> getAllEvents() {
        return eventRepo.findAllByDeletedFalse().stream()
                .map(DiagnosticEventDTO::new)
                .toList();
    }

    public DiagnosticResultDTO submitDiagnostic(DiagnosticSubmitDTO dto, Authentication authentication) {

        int score = dto.getSelectedEvents().stream()
                .mapToInt(data -> {
                    DiagnosticEvent event = eventRepo.findByIdAndDeletedFalse(data.getEventId())
                            .orElseThrow(() -> new NotFoundException("Événement introuvable"));
                    return event.getPoints() * data.getOccurrences();
                })
                .sum();

        String message = rangeService.getMessageForScore(score);

        if (authentication != null && authentication.isAuthenticated()) {
            User user = securityUtils.getCurrentUser(authentication);

            DiagnosticResult result = new DiagnosticResult();
            result.setUser(user);
            result.setScore(score);

            List<DiagnosticResultEvent> details = dto.getSelectedEvents().stream()
                    .map(data -> {
                        DiagnosticEvent event = eventRepo.findByIdAndDeletedFalse(data.getEventId())
                                .orElseThrow(() -> new NotFoundException("Événement introuvable"));

                        DiagnosticResultEvent resultEvent = new DiagnosticResultEvent();
                        resultEvent.setEvent(event);
                        resultEvent.setOccurrences(data.getOccurrences());
                        resultEvent.setResult(result);
                        return resultEvent;
                    })
                    .toList();

            result.setEventDetails(details);

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
        DiagnosticEvent event = eventRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));
        event.setLabel(dto.getLabel());
        event.setPoints(dto.getPoints());
        return new DiagnosticEventDTO(eventRepo.save(event));
    }

    public MessageDTO deleteEvent(Long id) {
        DiagnosticEvent event = eventRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Événement introuvable"));

        event.setDeleted(true);
        eventRepo.save(event);

        return new MessageDTO("Événement supprimé");
    }

    public List<DiagnosticHistoryDTO> getUserHistory(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);
        return resultRepo.findAllByUserId(user.getId()).stream()
                .map(result -> {
                    String message = rangeService.getMessageForScore(result.getScore());
                    return new DiagnosticHistoryDTO(result, message);
                })
                .toList();
    }
}
