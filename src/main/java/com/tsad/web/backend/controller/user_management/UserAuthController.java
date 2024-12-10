package com.tsad.web.backend.controller.user_management;

import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.controller.user_management.model.AddUserProfileRq;
import com.tsad.web.backend.controller.user_management.model.AddUserProfileRs;
import com.tsad.web.backend.controller.user_management.model.UserSearchRq;
import com.tsad.web.backend.controller.user_management.model.UserSearchRs;
import com.tsad.web.backend.service.user_management.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
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
    public List<UserSearchRs> searchUser(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                         @RequestBody UserSearchRq rq) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("searchUser() ... url: /user-list -> ID: {}, request: {}", userId, rq);
        List<UserSearchRs> rs = userManagementService.searchUser(rq);
        log.info("searchUser() ... url: /user-list -> done");
        return rs;
    }

    @PutMapping("/add-user")
    public AddUserProfileRs addUser(@RequestHeader(RequestHeaderName.USERNAME) String username,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                    @RequestBody AddUserProfileRq rq) {
        BigInteger userId = new BigInteger(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        log.info("addUser() ... url: /add-user -> ID: {}, request: {}", userId, rq);
        AddUserProfileRs rs = userManagementService.addUserByRegisterForm(userId, rq);
        log.info("addUser() ... url: /add-user -> done");
        return rs;
    }
}
