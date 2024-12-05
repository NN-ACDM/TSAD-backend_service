package com.tsad.web.backend.controller.authentication;

import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.controller.authentication.model.LoginRequest;
import com.tsad.web.backend.service.authentication.CredentialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CredentialService credentialService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws BusinessException {
        log.info("login() ... {}", request);
        String token = credentialService.login(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String headerToken) throws BusinessException {
        log.info("logout() ... {} is requesting logout", username);
        credentialService.logout(username, headerToken);
        return ResponseEntity.status(HttpStatus.OK).body("Logout");
    }
}
