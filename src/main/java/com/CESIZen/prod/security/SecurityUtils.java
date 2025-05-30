package com.CESIZen.prod.security;

import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.exception.BadRequestException;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new BadRequestException("Utilisateur non authentifié.");
        }

        User user = userRepository.findByUsernameAndDeletedFalse(authentication.getName());

        if (user == null) {
            throw new NotFoundException("Utilisateur non trouvé.");
        }

        return user;
    }
}
