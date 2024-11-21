package com.tsad.web.backend.config.auth;

import com.tsad.web.backend.repository.webservicedb.jpa.UserAuthJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
public class TokenService {

    @Autowired
    private UserAuthJpaRepository userAuthJpaRepository;


    public String generateToken() {
        String token = UUID.randomUUID().toString();
        while (!ObjectUtils.isEmpty(userAuthJpaRepository.findByToken(token))) {
            token = UUID.randomUUID().toString();
        }
        return token;
    }


    public void validateAndConsumeToken(String token) {
        UserAuthJpaEntity user = userAuthJpaRepository.findByToken(token);

        if (user == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        user.setToken(this.generateToken());
        userAuthJpaRepository.save(user);
    }

    public String extractToken(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}