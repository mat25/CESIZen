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
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
                RoleEnum.USER, "Rôle user",
                RoleEnum.MODERATOR, "Rôle modérateur",
                RoleEnum.ADMIN, "Rôle administrateur",
                RoleEnum.SUPER_ADMIN, "Rôle super-administrateur"
        );

        roleDescriptionMap.forEach((roleName, description) ->
                roleRepository.findByName(roleName).ifPresentOrElse(
                        role -> logger.info("Le rôle existe déjà : {}", role),
                        () -> {
                            Role roleToCreate = new Role();
                            roleToCreate.setName(roleName);
                            roleToCreate.setDescription(description);
                            roleRepository.save(roleToCreate);
                        }
                )
        );
    }

    public Optional<Role> findByName(RoleEnum name) {
        return roleRepository.findByName(name);
    }
}
