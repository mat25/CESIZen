package com.CESIZen.prod.service;

import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Recherche de l'utilisateur par username : {}", username);
        User user = userRepository.findByUsernameAndDeletedFalse(username);
        if (user == null) {
            logger.warn("Utilisateur introuvable pour le username : {}", username);
            throw new UsernameNotFoundException("Utilisateur introuvable pour le username : " + username);
        }
        logger.info("Utilisateur trouvé : {}", username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}