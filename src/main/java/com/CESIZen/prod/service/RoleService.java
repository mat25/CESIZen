package com.CESIZen.prod.service;

import com.CESIZen.prod.entity.Role;
import com.CESIZen.prod.entity.RoleEnum;
import com.CESIZen.prod.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


@Service
public class RoleService {

    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    void init() {
        if (!roleRepository.existsByName(RoleEnum.USER)) {
            logger.info("Role USER absent, création en cours.");
            Role roleUser = new Role();
            roleUser.setName(RoleEnum.USER);
            roleRepository.save(roleUser);
            logger.info("Role USER créé avec succès.");
        } else {
            logger.info("Role USER déjà existant.");
        }

        if (!roleRepository.existsByName(RoleEnum.ADMIN)) {
            logger.info("Role ADMIN absent, création en cours.");
            Role roleAdmin = new Role();
            roleAdmin.setName(RoleEnum.ADMIN);
            roleRepository.save(roleAdmin);
            logger.info("Role ADMIN créé avec succès.");
        } else {
            logger.info("Role ADMIN déjà existant.");
        }
    }
}
