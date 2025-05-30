package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticScoreRangeDTO;
import com.CESIZen.prod.entity.DiagnosticScoreRange;
import com.CESIZen.prod.exception.BadRequestException;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.DiagnosticScoreRangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticScoreRangeService {

    private static final Logger log = LoggerFactory.getLogger(DiagnosticScoreRangeService.class);

    private final DiagnosticScoreRangeRepository repository;

    public DiagnosticScoreRangeService(DiagnosticScoreRangeRepository repository) {
        this.repository = repository;
    }

    public String getMessageForScore(int score) {
        log.info("Recherche du message pour le score : {}", score);
        return repository.findAll().stream()
                .filter(range -> score >= range.getMinPoints() && score <= range.getMaxPoints())
                .map(DiagnosticScoreRange::getMessage)
                .findFirst()
                .orElse("Niveau inconnu");
    }

    public List<DiagnosticScoreRangeDTO> getAll() {
        log.info("Récupération de toutes les plages de score");
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public DiagnosticScoreRangeDTO create(DiagnosticScoreRangeDTO dto) {
        int newMin = dto.getMinPoints();
        int newMax = dto.getMaxPoints();

        log.info("Création d'une nouvelle plage de score [{} - {}]", newMin, newMax);

        boolean overlapExists = repository.findAll().stream().anyMatch(existing -> {
            int existingMin = existing.getMinPoints();
            int existingMax = existing.getMaxPoints();
            return newMin <= existingMax && newMax >= existingMin;
        });

        if (overlapExists) {
            log.warn("Chevauchement détecté pour la plage [{} - {}]", newMin, newMax);
            throw new BadRequestException("La plage [" + newMin + "–" + newMax + "] chevauche une plage existante.");
        }

        DiagnosticScoreRange entity = new DiagnosticScoreRange();
        entity.setMinPoints(newMin);
        entity.setMaxPoints(newMax);
        entity.setMessage(dto.getMessage());

        entity = repository.save(entity);
        log.info("Plage créée avec succès, id={}", entity.getId());
        return toDTO(entity);
    }

    public DiagnosticScoreRangeDTO update(Long id, DiagnosticScoreRangeDTO dto) {
        log.info("Mise à jour de la plage de score id={}", id);

        DiagnosticScoreRange existingRange = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Plage introuvable pour l'id={}", id);
                    return new NotFoundException("Plage introuvable");
                });

        int newMin = dto.getMinPoints();
        int newMax = dto.getMaxPoints();

        boolean overlapExists = repository.findAll().stream()
                .filter(range -> !range.getId().equals(id))
                .anyMatch(range ->
                        newMin <= range.getMaxPoints() && newMax >= range.getMinPoints()
                );

        if (overlapExists) {
            log.warn("Chevauchement détecté pour la mise à jour de la plage [{} - {}], id={}", newMin, newMax, id);
            throw new BadRequestException("La plage [" + newMin + "–" + newMax + "] chevauche une autre plage.");
        }

        existingRange.setMinPoints(newMin);
        existingRange.setMaxPoints(newMax);
        existingRange.setMessage(dto.getMessage());

        DiagnosticScoreRange updated = repository.save(existingRange);
        log.info("Plage mise à jour avec succès, id={}", updated.getId());
        return toDTO(updated);
    }

    public MessageDTO delete(Long id) {
        log.info("Suppression de la plage de score id={}", id);
        repository.deleteById(id);
        log.info("Plage supprimée, id={}", id);
        return new MessageDTO("Plage supprimée");
    }

    private DiagnosticScoreRangeDTO toDTO(DiagnosticScoreRange entity) {
        DiagnosticScoreRangeDTO dto = new DiagnosticScoreRangeDTO();
        dto.setId(entity.getId());
        dto.setMinPoints(entity.getMinPoints());
        dto.setMaxPoints(entity.getMaxPoints());
        dto.setMessage(entity.getMessage());
        return dto;
    }
}
