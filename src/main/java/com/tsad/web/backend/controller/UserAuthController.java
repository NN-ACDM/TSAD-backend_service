package com.tsad.web.backend.controller;

import com.tsad.web.backend.config.auth.model.LoginResponse;
import com.tsad.web.backend.controller.model.UserProfileRs;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/user-auth")
public class UserAuthController {

    @PostMapping("/user-list")
    public List<UserProfileRs> getUserList() {
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
}
