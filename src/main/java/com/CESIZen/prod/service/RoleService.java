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
            Role roleUser = new Role();
            roleUser.setName(RoleEnum.USER);
            roleRepository.save(roleUser);
        }

        if (!roleRepository.existsByName(RoleEnum.ADMIN)) {
            Role roleAdmin = new Role();
            roleAdmin.setName(RoleEnum.ADMIN);
            roleRepository.save(roleAdmin);
        }
    }
}
