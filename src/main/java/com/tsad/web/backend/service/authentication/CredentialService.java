package com.tsad.web.backend.service.authentication;

import com.tsad.web.backend.common.CryptoUtils;
import com.tsad.web.backend.common.ErrorCode;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.controller.authentication.model.EditCredentialRq;
import com.tsad.web.backend.controller.authentication.model.EditCredentialRs;
import com.tsad.web.backend.controller.authentication.model.LoginRequest;
import com.tsad.web.backend.controller.authentication.model.ValidateUsernameRs;
import com.tsad.web.backend.repository.webservicedb.jpa.UserAuthJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.UserProfileJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserProfileJpaEntity;
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

    private final UserProfileJpaRepository userProfileJpaRepository;
    private final UserAuthJpaRepository userAuthJpaRepository;
    private final CryptoUtils cryptoUtils;

    private final int USERNAME_MAX_LENGTH = 24;
    private final int PASSWORD_MAX_LENGTH = 64;

    public CredentialService(UserProfileJpaRepository userProfileJpaRepository,
                             UserAuthJpaRepository userAuthJpaRepository,
                             CryptoUtils cryptoUtils) {
        this.userProfileJpaRepository = userProfileJpaRepository;
        this.userAuthJpaRepository = userAuthJpaRepository;
        this.cryptoUtils = cryptoUtils;
    }

    private String generateToken() {
        //        while (!ObjectUtils.isEmpty(userAuthJpaRepository.findByToken(token))) {
//            log.error("generateToken() ... found duplicate token : {}", token);
//            token = UUID.randomUUID().toString();
//        }
        return UUID.randomUUID().toString();
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

        Optional<UserAuthJpaEntity> userOpt = userAuthJpaRepository.findByUsernameAndPassword(username, cryptoUtils.hashSHA384(password));
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

        Optional<UserAuthJpaEntity> userOpt = userAuthJpaRepository.findByUsernameAndAccessToken(headerUsername, token);
        if (userOpt.isPresent()) {
            UserAuthJpaEntity user = userOpt.get();
            log.debug("validateUsernameAndToken() ... authenticate passed -> user: {}", user.getUsername());
            return user;
        } else {
            log.warn("validateUsernameAndToken() ... {}", ErrorCode.CR0004);
            throw new BusinessException(HttpStatus.UNAUTHORIZED, ErrorCode.CR0004);
        }
    }

    private void validateUsernameFormat(String newUsername) throws BusinessException {
        if (!ObjectUtils.isEmpty(newUsername)) {
            if (newUsername.length() > USERNAME_MAX_LENGTH ||
                    !newUsername.matches("^[a-z0-9_]+$")) {
                log.error("validateUsernameFormat() ... {}", ErrorCode.UM0003);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0003);
            }
        }
    }

    private void validatePasswordFormat(String newPassword) throws BusinessException {
        if (!ObjectUtils.isEmpty(newPassword)) {
            if (newPassword.length() > PASSWORD_MAX_LENGTH) {
                log.error("validatePasswordFormat() ... {}", ErrorCode.UM0004);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0004);
            }
        }
    }

    public ValidateUsernameRs checkAvailableUsername(String newUsername) {
        ValidateUsernameRs rs = new ValidateUsernameRs();
        Optional<UserAuthJpaEntity> userAuthOpt = userAuthJpaRepository.findByUsername(newUsername);
        if (userAuthOpt.isPresent()) {
            log.warn("validateUsername() ... {}", ErrorCode.UM0014);
            rs.setAvailable(false);
            rs.setMessage(ErrorCode.UM0014.toString());
            return rs;
        }
        Optional<UserProfileJpaEntity> userProfileOpt = userProfileJpaRepository.findByProfessionalLicense(newUsername);
        if (userProfileOpt.isPresent()) {
            log.warn("validateUsername() ... {}", ErrorCode.UM0016);
            rs.setAvailable(false);
            rs.setMessage(ErrorCode.UM0016.toString());
            return rs;
        }
        try {
            this.validateUsernameFormat(newUsername);
        } catch (BusinessException ex) {
            log.warn("validateUsername() ... {}", ex.getMessage());
            rs.setAvailable(false);
            rs.setMessage(ex.getMessage());
            return rs;
        }
        rs.setAvailable(true);
        rs.setMessage("username is available");
        return rs;
    }

    public String login(LoginRequest rq) throws BusinessException {
        UserAuthJpaEntity user = this.validateUsernameAndPassword(rq.getUsername(), rq.getPassword());
        user.setAccessToken(this.generateToken());
        user.setLastLoginDatetime(new Date());
        userAuthJpaRepository.save(user);
        log.info("login() ... username: {} is successfully login", rq.getUsername());
        return user.getAccessToken();
    }

    public void logout(String username, String headerToken) throws BusinessException {
        UserAuthJpaEntity user = this.validateUsernameAndToken(username, headerToken);
        user.setAccessToken(null);
        userAuthJpaRepository.save(user);
        log.info("logout() ... username: {} is successfully logout", username);
    }

    public String rotateToken(String username, String headerToken) throws BusinessException {
        try {
            UserAuthJpaEntity user = this.validateUsernameAndToken(username, headerToken);
            String token = this.generateToken();
            user.setAccessToken(token);
            userAuthJpaRepository.save(user);
            log.debug("rotateToken() ... rotate token complete");
            return user.getAccessToken();
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
        return cryptoUtils.hashSHA384(input);
    }

    public EditCredentialRs editCredential(String username, EditCredentialRq rq) throws BusinessException {
        UserAuthJpaEntity existAuth = this.validateUsernameAndPassword(username, rq.getCurrentPassword());
        if (ObjectUtils.isEmpty(existAuth)) {
            log.warn("editCredential() ... {}", ErrorCode.CR0008);
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorCode.CR0008);
        }

        this.validateUsernameFormat(rq.getUsername());
        this.validatePasswordFormat(rq.getPassword());

        Date currentDate = new Date();
        EditCredentialRs rs = new EditCredentialRs();
        try {
            boolean isAuthChanged = false;
            if (!ObjectUtils.isEmpty(rq.getUsername()) && !existAuth.getUsername().equals(rq.getUsername())) {
                existAuth.setUsername(rq.getUsername());
                isAuthChanged = true;
            }
            if (!ObjectUtils.isEmpty(rq.getPassword()) && !existAuth.getPassword().equals(rq.getPassword())) {
                existAuth.setPassword(this.encryptPassword(rq.getPassword()));
                isAuthChanged = true;
            }
            if (isAuthChanged) {
                existAuth.setUpdateBy(existAuth.getId());
                existAuth.setUpdatedDatetime(currentDate);
                existAuth.setAccessToken(null);
            }

            UserAuthJpaEntity saveAuth = userAuthJpaRepository.save(existAuth);
            rs.setUsername(saveAuth.getUsername());
            rs.setPassword(null);
            return rs;
        } catch (Exception ex) {
            log.error("editCredential() ... {} / message: {}", ErrorCode.DB0003, ex.getMessage());
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.DB0003);
        }
    }
}
