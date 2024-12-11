package com.tsad.web.backend.controller.user_management;

import com.tsad.web.backend.controller.user_management.model.*;
import com.tsad.web.backend.service.user_management.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

import static com.tsad.web.backend.common.ErrorCode.CR0006;

@RestController
@RequestMapping("/admin/user")
public class UserAuthController {
    private static final Logger log = LoggerFactory.getLogger(UserAuthController.class);
    private static final String SEARCH_USER_URL = "/search";
    private static final String VALIDATE_USERNAME_URL = "/validate-username";
    private static final String VALIDATE_PROFESSIONAL_LICENSE_URL = "/validate-professional-license";
    private static final String ADD_USER_URL = "/add";
    private static final String EDIT_USER_URL = "/edit";
    private static final String DELETE_USER_URL = "/delete";

    private final UserManagementService userManagementService;

    public UserAuthController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    private BigInteger getUserIDFromSecurityContextHolder() {
        try {
            return new BigInteger(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        } catch (Exception ex) {
            log.warn("getUserIDFromSecurityContextHolder() ... {}", CR0006);
            return null;
        }
    }

    @GetMapping(SEARCH_USER_URL)
    public List<SearchUserRs> searchUser(@RequestBody SearchUserRq rq) {
        BigInteger userId = this.getUserIDFromSecurityContextHolder();
        log.info("searchUser() ... url: {} -> ID: {}, request: {}", SEARCH_USER_URL, userId, rq);
        List<SearchUserRs> rs = userManagementService.searchUser(rq);
        log.info("searchUser() ... url: {} -> done", SEARCH_USER_URL);
        return rs;
    }

    @PostMapping(VALIDATE_USERNAME_URL)
    public ValidateUsernameRs validateUsername(@RequestBody ValidateUsernameRq rq) {
        BigInteger userId = this.getUserIDFromSecurityContextHolder();
        log.info("validateUsername() ... url: {} -> ID: {}, request: {}", VALIDATE_USERNAME_URL, userId, rq);
        ValidateUsernameRs rs = userManagementService.validateUsername(rq.getUsername());
        log.info("validateUsername() ... url: {} -> done", VALIDATE_USERNAME_URL);
        return rs;
    }

    @PostMapping(VALIDATE_PROFESSIONAL_LICENSE_URL)
    public ValidateProfessionalLcRs validateProfessionalLicense(@RequestBody ValidateProfessionalLcRq rq) {
        BigInteger userId = this.getUserIDFromSecurityContextHolder();
        log.info("validateProfessionalLicense() ... url: {} -> ID: {}, request: {}", VALIDATE_PROFESSIONAL_LICENSE_URL, userId, rq);
        ValidateProfessionalLcRs rs = userManagementService.validateProfessionalLicense(rq.getProfessionalLicense());
        log.info("validateProfessionalLicense() ... url: {} -> done", VALIDATE_PROFESSIONAL_LICENSE_URL);
        return rs;
    }

    @PostMapping(ADD_USER_URL)
    public AddUserRs addUser(@RequestBody AddUserRq rq) {
        BigInteger userId = this.getUserIDFromSecurityContextHolder();
        log.info("addUser() ... url: {} -> ID: {}, request: {}", ADD_USER_URL, userId, rq);
        AddUserRs rs = userManagementService.addUser(userId, rq);
        log.info("addUser() ... url: {} -> done", ADD_USER_URL);
        return rs;
    }

    @PatchMapping(EDIT_USER_URL)
    public EditUserRs editUserByForm(@RequestBody EditUserRq rq) {
        BigInteger userId = this.getUserIDFromSecurityContextHolder();
        log.info("editUserByForm() ... url: {} -> ID: {}, request: {}", EDIT_USER_URL, userId, rq);
        EditUserRs rs = userManagementService.editUserByForm(userId, rq);
        log.info("editUserByForm() ... url: {} -> done", EDIT_USER_URL);
        return rs;
    }

    @DeleteMapping(DELETE_USER_URL)
    public ResponseEntity<?> deleteUser(@RequestBody DeleteUserRq rq) {
        BigInteger userId = this.getUserIDFromSecurityContextHolder();
        log.info("deleteUser() ... url: {} -> ID: {}, request: {}", DELETE_USER_URL, userId, rq);
        userManagementService.deleteUser(rq);
        log.info("deleteUser() ... url: {} -> done", DELETE_USER_URL);
        return ResponseEntity.status(HttpStatus.OK).body("delete user success");
    }
}
