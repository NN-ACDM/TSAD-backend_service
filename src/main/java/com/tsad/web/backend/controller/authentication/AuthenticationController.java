package com.tsad.web.backend.controller.authentication;

import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.controller.authentication.model.LoginRequest;
import com.tsad.web.backend.service.authentication.CredentialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private final CredentialService credentialService;

    public AuthenticationController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("login() ... username: {} is trying to login", request.getUsername());
        String token = credentialService.login(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("login success");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String headerToken) throws BusinessException {
        log.info("logout() ... username: {} is requesting logout", username);
        credentialService.logout(username, headerToken);
        return ResponseEntity.status(HttpStatus.OK).body("logout success");
    }
}
