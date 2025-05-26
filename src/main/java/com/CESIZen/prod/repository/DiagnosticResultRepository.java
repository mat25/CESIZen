package com.CESIZen.prod.repository;

import com.CESIZen.prod.entity.DiagnosticResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosticResultRepository extends JpaRepository<DiagnosticResult, Long> {

    List<DiagnosticResult> findAllByUserId(Long userId);
}
