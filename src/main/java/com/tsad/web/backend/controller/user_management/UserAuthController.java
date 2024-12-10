package com.tsad.web.backend.controller.user_management;

import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.controller.user_management.model.UserProfileRq;
import com.tsad.web.backend.controller.user_management.model.UserSearchRq;
import com.tsad.web.backend.controller.user_management.model.UserSearchRs;
import com.tsad.web.backend.service.user_management.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class UserAuthController {
    private static final Logger log = LoggerFactory.getLogger(UserAuthController.class);

    private final UserManagementService userManagementService;

    public UserAuthController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PostMapping("/user-list")
    public List<UserSearchRs> getUserList(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                          @RequestBody UserSearchRq rq) {

        Object userIdOp = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userIdOp.toString();
        log.info("searchUser() ... url: /user-list -> ID: {}, request: {}", userId, rq);
        List<UserSearchRs> rsList = userManagementService.searchUser(rq);
        log.info("searchUser() ... url: /user-list -> done");
        return rsList;
    }

    @PutMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                     @RequestBody UserProfileRq rq) {

//        userManagementService.addUser(user.getId(), rq);
        return ResponseEntity.status(HttpStatus.OK).body("User added");
    }
}
