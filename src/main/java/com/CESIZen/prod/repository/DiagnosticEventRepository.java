package com.CESIZen.prod.repository;

import com.CESIZen.prod.entity.DiagnosticEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticEventRepository extends JpaRepository<DiagnosticEvent, Long> {
}
