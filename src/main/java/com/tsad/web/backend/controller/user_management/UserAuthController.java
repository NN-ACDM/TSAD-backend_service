package com.tsad.web.backend.controller.user_management;

import com.tsad.web.backend.auth.CredentialService;
import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.controller.user_management.model.UserProfileRq;
import com.tsad.web.backend.repository.webservicedb.jpa.UserAuthJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import com.tsad.web.backend.service.user_management.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/user")
public class UserAuthController {

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private UserAuthJpaRepository userAuthJpaRepository;

    @PutMapping("/add")
    public ResponseEntity<?> addUser(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                     @RequestBody UserProfileRq rq) {

        String token = credentialService.extractToken(authorizationHeader);

        if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing necessary header");
        }

        UserAuthJpaEntity user = userAuthJpaRepository.findByToken(token);

        if (user == null || !user.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        userManagementService.addUser(user.getId(), rq);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
