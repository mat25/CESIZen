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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticService {

    private static final Logger log = LoggerFactory.getLogger(DiagnosticService.class);

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
        log.info("Récupération de tous les événements diagnostics");
        return eventRepo.findAllByDeletedFalse().stream()
                .map(DiagnosticEventDTO::new)
                .toList();
    }

    public DiagnosticResultDTO submitDiagnostic(DiagnosticSubmitDTO dto, Authentication authentication) {
        log.info("Soumission d'un diagnostic avec {} événements sélectionnés", dto.getSelectedEvents().size());

        int score = dto.getSelectedEvents().stream()
                .mapToInt(data -> {
                    DiagnosticEvent event = eventRepo.findByIdAndDeletedFalse(data.getEventId())
                            .orElseThrow(() -> {
                                log.warn("Événement introuvable pour l'id={}", data.getEventId());
                                return new NotFoundException("Événement introuvable");
                            });
                    return event.getPoints() * data.getOccurrences();
                })
                .sum();

        log.info("Score total calculé : {}", score);

        String message = rangeService.getMessageForScore(score);

        if (authentication != null && authentication.isAuthenticated()) {
            User user = securityUtils.getCurrentUser(authentication);
            log.info("Utilisateur connecté : id={}, soumission du résultat du diagnostic", user.getId());

            DiagnosticResult result = new DiagnosticResult();
            result.setUser(user);
            result.setScore(score);

            List<DiagnosticResultEvent> details = dto.getSelectedEvents().stream()
                    .map(data -> {
                        DiagnosticEvent event = eventRepo.findByIdAndDeletedFalse(data.getEventId())
                                .orElseThrow(() -> {
                                    log.warn("Événement introuvable pour l'id={}", data.getEventId());
                                    return new NotFoundException("Événement introuvable");
                                });

                        DiagnosticResultEvent resultEvent = new DiagnosticResultEvent();
                        resultEvent.setEvent(event);
                        resultEvent.setOccurrences(data.getOccurrences());
                        resultEvent.setResult(result);
                        return resultEvent;
                    })
                    .toList();

            result.setEventDetails(details);

            resultRepo.save(result);
            log.info("Diagnostic sauvegardé avec succès, id={}", result.getId());
        } else {
            log.info("Soumission du diagnostic anonyme (non authentifié)");
        }

        return new DiagnosticResultDTO(score, message);
    }


    public DiagnosticEventDTO createEvent(DiagnosticEventDTO dto) {
        log.info("Création d'un nouvel événement diagnostic avec label='{}', points={}", dto.getLabel(), dto.getPoints());
        DiagnosticEvent event = new DiagnosticEvent();
        event.setLabel(dto.getLabel());
        event.setPoints(dto.getPoints());
        event = eventRepo.save(event);
        log.info("Événement créé avec succès, id={}", event.getId());
        return new DiagnosticEventDTO(event);
    }

    public DiagnosticEventDTO updateEvent(Long id, DiagnosticEventDTO dto) {
        log.info("Mise à jour de l'événement diagnostic id={}", id);
        DiagnosticEvent event = eventRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Événement introuvable pour l'id={}", id);
                    return new RuntimeException("Événement introuvable");
                });
        event.setLabel(dto.getLabel());
        event.setPoints(dto.getPoints());
        event = eventRepo.save(event);
        log.info("Événement mis à jour avec succès, id={}", id);
        return new DiagnosticEventDTO(event);
    }

    public MessageDTO deleteEvent(Long id) {
        log.info("Suppression logique de l'événement diagnostic id={}", id);
        DiagnosticEvent event = eventRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Événement introuvable pour suppression, id={}", id);
                    return new NotFoundException("Événement introuvable");
                });

        event.setDeleted(true);
        eventRepo.save(event);

        log.info("Événement marqué comme supprimé, id={}", id);
        return new MessageDTO("Événement supprimé");
    }

    public List<DiagnosticHistoryDTO> getUserHistory(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);
        log.info("Récupération de l'historique des diagnostics pour l'utilisateur id={}", user.getId());

        return resultRepo.findAllByUserId(user.getId()).stream()
                .map(result -> {
                    String message = rangeService.getMessageForScore(result.getScore());
                    return new DiagnosticHistoryDTO(result, message);
                })
                .toList();
    }
}
