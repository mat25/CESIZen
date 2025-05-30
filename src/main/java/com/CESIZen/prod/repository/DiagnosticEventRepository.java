package com.CESIZen.prod.repository;

import com.CESIZen.prod.entity.DiagnosticEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiagnosticEventRepository extends JpaRepository<DiagnosticEvent, Long> {

    List<DiagnosticEvent> findAllByDeletedFalse();

    Optional<DiagnosticEvent> findByIdAndDeletedFalse(Long id);
}
