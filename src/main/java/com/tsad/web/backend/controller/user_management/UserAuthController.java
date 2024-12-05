package com.tsad.web.backend.controller.user_management;

import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.controller.user_management.model.UserProfileRq;
import com.tsad.web.backend.service.authentication.CredentialService;
import com.tsad.web.backend.service.user_management.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class UserAuthController {
    private static final Logger log = LoggerFactory.getLogger(UserAuthController.class);

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private UserManagementService userManagementService;

    @PostMapping("/user-list")
    public ResponseEntity<?> getUserList(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {

//        userManagementService.addUser(user.getId(), rq);
        log.info("getUserList() ... authToken: {}", authToken);
        return ResponseEntity.status(HttpStatus.OK).body("User list");
    }

    @PutMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                     @RequestBody UserProfileRq rq) {

//        userManagementService.addUser(user.getId(), rq);
        return ResponseEntity.status(HttpStatus.OK).body("User added");
    }
}
