package com.tsad.web.backend.controller.authentication;

import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.controller.authentication.model.*;
import com.tsad.web.backend.service.authentication.CredentialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

import static com.tsad.web.backend.common.ErrorCode.CR0006;


@RestController
public class CredentialController {
    private static final Logger log = LoggerFactory.getLogger(CredentialController.class);

    private static final String VALIDATE_USERNAME_URL = "/auth/validate-username";
    private static final String EDIT_CREDENTIAL_URL = "/auth/edit-credential";

    @Autowired
    private CredentialService credentialService;

    private BigInteger getUserIDFromSecurityContextHolder() {
        try {
            return new BigInteger(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        } catch (Exception ex) {
            log.warn("getUserIDFromSecurityContextHolder() ... {}", CR0006);
            return null;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws BusinessException {
        log.info("login() ... username: {} is trying to login", request.getUsername());
        String token = credentialService.login(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String headerToken) throws BusinessException {
        log.info("logout() ... username: {} is requesting logout", username);
        credentialService.logout(username, headerToken);
        return ResponseEntity.status(HttpStatus.OK).body("logout success");
    }

    @PostMapping(VALIDATE_USERNAME_URL)
    public ValidateUsernameRs validateUsername(@RequestBody ValidateUsernameRq rq) {
        BigInteger userId = this.getUserIDFromSecurityContextHolder();
        log.info("validateUsername() ... url: {} -> ID: {} validate their new username", VALIDATE_USERNAME_URL, userId);
        return credentialService.checkAvailableUsername(rq.getUsername());
    }

    @PatchMapping(EDIT_CREDENTIAL_URL)
    public EditCredentialRs editCredential(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                           @RequestBody EditCredentialRq rq) throws BusinessException {
        BigInteger userId = this.getUserIDFromSecurityContextHolder();
        log.info("editCredential() ... url: {} -> ID: {} is changing their credential", EDIT_CREDENTIAL_URL, userId);
        return credentialService.editCredential(username, rq);
    }
}
