package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticScoreRangeDTO;
import com.CESIZen.prod.entity.DiagnosticScoreRange;
import com.CESIZen.prod.exception.BadRequestException;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.DiagnosticScoreRangeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticScoreRangeService {

    private final DiagnosticScoreRangeRepository repository;

    public DiagnosticScoreRangeService(DiagnosticScoreRangeRepository repository) {
        this.repository = repository;
    }

    public List<DiagnosticScoreRangeDTO> getAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public DiagnosticScoreRangeDTO create(DiagnosticScoreRangeDTO dto) {
        int newMin = dto.getMinPoints();
        int newMax = dto.getMaxPoints();

        boolean overlapExists = repository.findAll().stream().anyMatch(existing -> {
            int existingMin = existing.getMinPoints();
            int existingMax = existing.getMaxPoints();
            return newMin <= existingMax && newMax >= existingMin;
        });

        if (overlapExists) {
            throw new BadRequestException("La plage [" + newMin + "–" + newMax + "] chevauche une plage existante.");
        }

        DiagnosticScoreRange entity = new DiagnosticScoreRange();
        entity.setMinPoints(newMin);
        entity.setMaxPoints(newMax);
        entity.setMessage(dto.getMessage());

        entity = repository.save(entity);
        return toDTO(entity);
    }

    public DiagnosticScoreRangeDTO update(Long id, DiagnosticScoreRangeDTO dto) {
        DiagnosticScoreRange existingRange = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Plage introuvable"));

        int newMin = dto.getMinPoints();
        int newMax = dto.getMaxPoints();

        boolean overlapExists = repository.findAll().stream()
                .filter(range -> !range.getId().equals(id))
                .anyMatch(range ->
                        newMin <= range.getMaxPoints() && newMax >= range.getMinPoints()
                );

        if (overlapExists) {
            throw new BadRequestException("La plage [" + newMin + "–" + newMax + "] chevauche une autre plage.");
        }

        existingRange.setMinPoints(newMin);
        existingRange.setMaxPoints(newMax);
        existingRange.setMessage(dto.getMessage());

        DiagnosticScoreRange updated = repository.save(existingRange);
        return toDTO(updated);
    }

    public MessageDTO delete(Long id) {
        repository.deleteById(id);
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
