package com.tsad.web.backend.service.authentication;

import com.tsad.web.backend.common.CryptoUtils;
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
    private final CryptoUtils cryptoUtils;

    public CredentialService(UserAuthJpaRepository userAuthJpaRepository,
                             CryptoUtils cryptoUtils) {
        this.userAuthJpaRepository = userAuthJpaRepository;
        this.cryptoUtils = cryptoUtils;
    }

    private String generateToken() {
        String token = UUID.randomUUID().toString();
        while (!ObjectUtils.isEmpty(userAuthJpaRepository.findByToken(token))) {
            log.error("generateToken() ... found duplicate token : {}", token);
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
            log.info("validateUsernameAndPassword() ... {}", ErrorCode.CR0002);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0002);
        }
        if (ObjectUtils.isEmpty(password)) {
            log.info("validateUsernameAndPassword() ... {}", ErrorCode.CR0003);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0003);
        }

        Optional<UserAuthJpaEntity> userOpt = userAuthJpaRepository.findByUsernameAndPassword(username, password);
        if (userOpt.isEmpty()) {
            log.info("validateUsernameAndPassword() ... {}", ErrorCode.CR0005);
            throw new BusinessException(HttpStatus.UNAUTHORIZED, ErrorCode.CR0005);
        } else if (!userOpt.get().isActive()) {
            log.warn("validateUsernameAndPassword() ... {}", ErrorCode.CR0007);
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorCode.CR0007);
        } else {
            return userOpt.get();
        }
    }

    public UserAuthJpaEntity validateUsernameAndToken(String headerUsername, String headerToken) throws BusinessException {
        String token = this.extractToken(headerToken);
        if (ObjectUtils.isEmpty(headerUsername)) {
            log.warn("validateUsernameAndToken() ... {}", ErrorCode.CR0002);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0002);
        }
        if (ObjectUtils.isEmpty(token)) {
            log.warn("validateUsernameAndToken() ... {}", ErrorCode.CR0001);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.CR0001);
        }

        Optional<UserAuthJpaEntity> userOpt = userAuthJpaRepository.findByUsernameAndToken(headerUsername, token);
        if (userOpt.isPresent()) {
            UserAuthJpaEntity user = userOpt.get();
            log.debug("validateUsernameAndToken() ... authenticate passed -> user: {}", user.getUsername());
            return user;
        } else {
            log.warn("validateUsernameAndToken() ... {}", ErrorCode.CR0004);
            throw new BusinessException(HttpStatus.UNAUTHORIZED, ErrorCode.CR0004);
        }
    }

    public String login(LoginRequest rq) {
        UserAuthJpaEntity user = this.validateUsernameAndPassword(rq.getUsername(), rq.getPassword());
        user.setToken(this.generateToken());
        user.setLastLoginDatetime(new Date());
        userAuthJpaRepository.save(user);
        log.info("login() ... username: {} is successfully login", rq.getUsername());
        return user.getToken();
    }

    public void logout(String username, String headerToken) {
        UserAuthJpaEntity user = this.validateUsernameAndToken(username, headerToken);
        user.setToken(null);
        userAuthJpaRepository.save(user);
        log.info("logout() ... username: {} is successfully logout", username);
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
            log.error("rotateToken() ... {} / message: {}", ErrorCode.DB0001, ex.toString());
            throw ex;
        }
    }

    public String generateDefaultPassword(String username) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return cryptoUtils.hashSHA256(String.format("TSAD-%s?%s", calendar.get(Calendar.YEAR), username));
    }

    public String encryptPassword(String input) {
        return cryptoUtils.bCryptPasswordEncoder(input);
    }
}
