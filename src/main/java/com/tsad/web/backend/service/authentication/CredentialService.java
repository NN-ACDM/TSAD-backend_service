package com.tsad.web.backend.service.authentication;

import com.tsad.web.backend.common.ErrorCode;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.controller.authentication.model.LoginRequest;
import com.tsad.web.backend.repository.webservicedb.jpa.UserAuthJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@Service
public class CredentialService {
    private static final Logger log = LoggerFactory.getLogger(CredentialService.class);

    private final UserAuthJpaRepository userAuthJpaRepository;

    public CredentialService(UserAuthJpaRepository userAuthJpaRepository) {
        this.userAuthJpaRepository = userAuthJpaRepository;
    }

    private String generateToken() {
        String token = UUID.randomUUID().toString();
        while (!ObjectUtils.isEmpty(userAuthJpaRepository.findByToken(token))) {
            log.warn("generateToken() ... found duplicate token : {}", token);
            token = UUID.randomUUID().toString();
        }
        return token;
    }

    private String extractToken(String headerToken) throws BusinessException {
        if (headerToken != null && headerToken.startsWith("Bearer ")) {
            return headerToken.substring(7);
        } else {
            log.error("extractToken() ... {}", ErrorCode.CR0001);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0001);
        }
    }

    private UserAuthJpaEntity validateUsernameAndPassword(String username, String password) throws BusinessException {
        if (ObjectUtils.isEmpty(username)) {
            log.error("validateUsernameAndPassword() ... {}", ErrorCode.CR0002);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0002);
        }
        if (ObjectUtils.isEmpty(password)) {
            log.error("validateUsernameAndPassword() ... {}", ErrorCode.CR0003);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0003);
        }

        Optional<UserAuthJpaEntity> userOpt = userAuthJpaRepository.findByUsernameAndPassword(username, password);
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            log.error("validateUsernameAndPassword() ... {}", ErrorCode.CR0005);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0005);
        }
    }

    public UserAuthJpaEntity validateUsernameAndToken(String headerUsername, String headerToken) throws BusinessException {
        String token = this.extractToken(headerToken);
        if (ObjectUtils.isEmpty(headerUsername)) {
            log.error("validateUsernameAndToken() ... {}", ErrorCode.CR0002);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0002);
        }
        if (ObjectUtils.isEmpty(token)) {
            log.error("validateUsernameAndToken() ... {}", ErrorCode.CR0001);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0001);
        }

        Optional<UserAuthJpaEntity> userOpt = userAuthJpaRepository.findByUsernameAndToken(headerUsername, token);
        if (userOpt.isPresent()) {
            UserAuthJpaEntity user = userOpt.get();
            log.debug("validateUsernameAndToken() ... authenticate passed -> user: {}", user.getUsername());
            return user;
        } else {
            log.error("validateUsernameAndToken() ... {}", ErrorCode.CR0004);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0004);
        }
    }

    public String login(LoginRequest rq) throws BusinessException {
        try {
            UserAuthJpaEntity user = this.validateUsernameAndPassword(rq.getUsername(), rq.getPassword());
            user.setToken(this.generateToken());
            userAuthJpaRepository.save(user);
            log.info("login() ... login complete");
            return user.getToken();
        } catch (Exception ex) {
            log.error("login() ... {}", ErrorCode.DB0001);
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DB0001);
        }
    }

    public void logout(String username, String headerToken) throws BusinessException {
        try {
            UserAuthJpaEntity user = this.validateUsernameAndToken(username, headerToken);
            user.setToken(null);
            userAuthJpaRepository.save(user);
            log.info("logout() ... logout complete");
        } catch (Exception ex) {
            log.error("logout() ... {}", ErrorCode.DB0002);
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DB0002);
        }
    }

    public String rotateToken(String username, String headerToken) throws BusinessException {
        try {
            UserAuthJpaEntity user = this.validateUsernameAndToken(username, headerToken);
            String token = this.generateToken();
            user.setToken(token);
            userAuthJpaRepository.save(user);
            log.debug("rotateToken() ... rotate token complete");
            return user.getToken();
        } catch (Exception ex) {
            log.error("rotateToken() ... {}", ErrorCode.DB0001);
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DB0001);
        }
    }

    public String generateDefaultPassword(String username) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return String.format("TSAD-%s?%s", calendar.get(Calendar.YEAR), username);
    }
}
