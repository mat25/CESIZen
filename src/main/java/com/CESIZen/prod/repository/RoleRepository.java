package com.CESIZen.prod.repository;

import com.CESIZen.prod.entity.Role;
import com.CESIZen.prod.entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum name);
    boolean existsByName(RoleEnum name);
}
