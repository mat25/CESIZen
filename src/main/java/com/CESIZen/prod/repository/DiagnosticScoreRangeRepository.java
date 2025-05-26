package com.CESIZen.prod.repository;

import com.CESIZen.prod.entity.DiagnosticScoreRange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiagnosticScoreRangeRepository extends JpaRepository<DiagnosticScoreRange, Long> {
    Optional<DiagnosticScoreRange> findFirstByMinPointsLessThanEqualAndMaxPointsGreaterThanEqual(int min, int max);
}

