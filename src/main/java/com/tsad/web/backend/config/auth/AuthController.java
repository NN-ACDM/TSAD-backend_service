package com.tsad.web.backend.config.auth;

import com.tsad.web.backend.config.auth.model.LoginRequest;
import com.tsad.web.backend.config.auth.model.LoginResponse;
import com.tsad.web.backend.repository.webservicedb.jpa.UserAuthJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@RequestMapping("/tsad/auth")
public class AuthController {

    @Autowired
    private UserAuthJpaRepository userAuthJpaRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        UserAuthJpaEntity user = userAuthJpaRepository.findByUsername(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword())) {
            //  Generate token
            String token = tokenService.generateToken();

            user.setToken(token);
            user.setUpdatedDatetime(new Date());
            userAuthJpaRepository.save(user);

            return ResponseEntity.ok(new LoginResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = tokenService.extractToken(authorizationHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        UserAuthJpaEntity user = userAuthJpaRepository.findByToken(token);
        if (user != null) {

            user.setToken(null);
            user.setUpdatedDatetime(new Date());
            userAuthJpaRepository.save(user);

            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
