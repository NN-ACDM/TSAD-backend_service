package com.tsad.web.backend.controller.user_management;

import com.tsad.web.backend.auth.CredentialService;
import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.controller.user_management.model.UserProfileRq;
import com.tsad.web.backend.service.user_management.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public")
public class UserAuthController {

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private UserManagementService userManagementService;

    @PutMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                     @RequestBody UserProfileRq rq) {

//        userManagementService.addUser(user.getId(), rq);

        return ResponseEntity.status(HttpStatus.OK).body("User added");
    }
}
