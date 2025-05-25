package com.CESIZen.prod.repository;

import com.CESIZen.prod.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}

