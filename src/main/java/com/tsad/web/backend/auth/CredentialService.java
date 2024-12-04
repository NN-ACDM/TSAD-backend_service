package com.tsad.web.backend.auth;

import com.tsad.web.backend.auth.model.LoginRequest;
import com.tsad.web.backend.repository.webservicedb.jpa.UserAuthJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class CredentialService {

    @Autowired
    private UserAuthJpaRepository userAuthJpaRepository;

    private String generateToken() {
        String token = UUID.randomUUID().toString();
        while (!ObjectUtils.isEmpty(userAuthJpaRepository.findByToken(token))) {
            token = UUID.randomUUID().toString();
        }
        return token;
    }

//    private String extractTokenBearer(String authorizationHeader) {
//        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
//            return authorizationHeader.substring(7);
//        }
//        return null;
//    }

    public UserAuthJpaEntity validateToken(String token) {
        Optional<UserAuthJpaEntity> userOpt = userAuthJpaRepository.findByToken(token);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }
        throw new IllegalArgumentException("Invalid or expired token");
    }

    public String login(LoginRequest rq) {
        Optional<UserAuthJpaEntity> userOpt = userAuthJpaRepository.findByUsernameAndPassword(rq.getUsername(), rq.getPassword());
        if (userOpt.isPresent()) {
            UserAuthJpaEntity user = userOpt.get();
            if (user.getPassword().equals(rq.getPassword())) {
                user.setToken(this.generateToken());
                userAuthJpaRepository.save(user);
                return user.getToken();
            } else {
                return null;
            }
        }
        throw new IllegalArgumentException("Invalid username or password");
    }

    public boolean logout(String token) {
        try {
            UserAuthJpaEntity user = this.validateToken(token);
            user.setToken(null);
            userAuthJpaRepository.save(user);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String rotateToken(String currentToken) {
        UserAuthJpaEntity user = this.validateToken(currentToken);
        String token = UUID.randomUUID().toString();
        while (!ObjectUtils.isEmpty(userAuthJpaRepository.findByToken(token))) {
            token = UUID.randomUUID().toString();
        }
        user.setToken(token);
        userAuthJpaRepository.save(user);
        return user.getToken();
    }

    public String generateDefaultPassword(String username) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return String.format("TSAD-%s?%s", calendar.get(Calendar.YEAR), username);
    }
}