package com.tsad.web.backend.service.user_management;

import com.tsad.web.backend.common.DateFormat;
import com.tsad.web.backend.common.ErrorCode;
import com.tsad.web.backend.common.UserLevel;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.controller.user_management.model.*;
import com.tsad.web.backend.repository.webservicedb.jdbc.UserJdbcRepository;
import com.tsad.web.backend.repository.webservicedb.jdbc.model.UserProfileSearchJdbcEntity;
import com.tsad.web.backend.repository.webservicedb.jpa.UserAuthJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.UserProfileJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserProfileJpaEntity;
import com.tsad.web.backend.service.authentication.CredentialService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserManagementService {
    private static final Logger log = LoggerFactory.getLogger(UserManagementService.class);

    private final UserProfileJpaRepository userProfileJpaRepository;
    private final UserAuthJpaRepository userAuthJpaRepository;
    private final UserJdbcRepository userJdbcRepository;
    private final CredentialService credentialService;

    public UserManagementService(UserProfileJpaRepository userProfileJpaRepository,
                                 UserAuthJpaRepository userAuthJpaRepository,
                                 UserJdbcRepository userJdbcRepository,
                                 CredentialService credentialService) {
        this.userProfileJpaRepository = userProfileJpaRepository;
        this.userAuthJpaRepository = userAuthJpaRepository;
        this.userJdbcRepository = userJdbcRepository;
        this.credentialService = credentialService;
    }

    private final int USERNAME_MAX_LENGTH = 24;
    private final int PASSWORD_MAX_LENGTH = 48;
    private final int FIRST_NAME_MAX_LENGTH = 48;
    private final int LAST_NAME_MAX_LENGTH = 48;
    private final int EMAIL_MAX_LENGTH = 32;
    private final int MOBILE_MAX_LENGTH = 20;
    private final int PROFESSIONAL_LICENSE_MAX_LENGTH = 20;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat.DATETIME.toString());

    private void validateUsernameFormat(String newUsername) {
        if (!ObjectUtils.isEmpty(newUsername)) {
            if (newUsername.length() > USERNAME_MAX_LENGTH ||
                    !newUsername.matches("^[a-z0-9_]+$")) {
                log.error("validateUsernameFormat() ... {}", ErrorCode.UM0003);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0003);
            }
        }
    }

    private void validatePasswordFormat(String newPassword) {
        if (!ObjectUtils.isEmpty(newPassword)) {
            if (newPassword.length() > PASSWORD_MAX_LENGTH) {
                log.error("validatePasswordFormat() ... {}", ErrorCode.UM0004);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0004);
            }
        }
    }

    private void validateFirstNameFormat(String newFirstName) {
        if (!ObjectUtils.isEmpty(newFirstName)) {
            if (newFirstName.length() > FIRST_NAME_MAX_LENGTH ||
                    !newFirstName.matches("[a-zA-Z]+")) {
                log.error("validateFirstNameFormat() ... {}", ErrorCode.UM0005);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0005);
            }
        }
    }

    private void validateLastNameFormat(String newLastName) {
        if (!ObjectUtils.isEmpty(newLastName)) {
            if (newLastName.length() > LAST_NAME_MAX_LENGTH ||
                    !newLastName.matches("[a-zA-Z]+")) {
                log.error("validateLastNameFormat() ... {}", ErrorCode.UM0006);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0006);
            }
        }
    }

    private void validateEmailFormat(String newEmail) {
        if (!ObjectUtils.isEmpty(newEmail)) {
            if (newEmail.length() > EMAIL_MAX_LENGTH ||
                    !newEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                log.error("validateEmailFormat() ... {}", ErrorCode.UM0007);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0007);
            }
        }
    }

    private void validateMobileFormat(String newMobile) {
        if (!ObjectUtils.isEmpty(newMobile)) {
            if (newMobile.length() > MOBILE_MAX_LENGTH ||
                    !newMobile.matches("^[0-9]+$")) {
                log.error("validateMobileFormat() ... {}", ErrorCode.UM0008);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0008);
            }
        }
    }

    private void validateProfessionalLicenseFormat(String newProfessionalLicense) {
        if (!ObjectUtils.isEmpty(newProfessionalLicense)) {
            if (newProfessionalLicense.length() > PROFESSIONAL_LICENSE_MAX_LENGTH ||
                    !newProfessionalLicense.matches("^[a-z0-9_]+$")) {
                log.error("validateProfessionalLicenseFormat() ... {}", ErrorCode.UM0009);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0009);
            }
        }
    }

    private void validateLevelFormat(String newLevel) {
        if (!ObjectUtils.isEmpty(newLevel)) {
            if (!(UserLevel.MEMBER.toString().equals(newLevel) ||
                    UserLevel.ADMIN.toString().equals(newLevel) ||
                    UserLevel.MASTER.toString().equals(newLevel))) {
                log.error("validateLevelFormat() ... {}", ErrorCode.UM0010);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0010);
            }
        }
    }

    public ValidateUsernameRs validateUsername(String newUsername) {
        ValidateUsernameRs rs = new ValidateUsernameRs();
        Optional<UserAuthJpaEntity> userAuthOpt = userAuthJpaRepository.findByUsername(newUsername);
        if (userAuthOpt.isPresent()) {
            log.warn("validateUsername() ... {}", ErrorCode.UM0014);
            rs.setAvailable(false);
            rs.setMessage(ErrorCode.UM0014.toString());
            return rs;
        }
        Optional<UserProfileJpaEntity> userProfileOpt = userProfileJpaRepository.findByProfessionalLicense(newUsername);
        if (userProfileOpt.isPresent()) {
            log.warn("validateUsername() ... {}", ErrorCode.UM0016);
            rs.setAvailable(false);
            rs.setMessage(ErrorCode.UM0016.toString());
            return rs;
        }
        try {
            this.validateUsernameFormat(newUsername);
        } catch (BusinessException ex) {
            log.warn("validateUsername() ... {}", ex.getMessage());
            rs.setAvailable(false);
            rs.setMessage(ex.getMessage());
            return rs;
        }
        rs.setAvailable(true);
        rs.setMessage("username is available");
        return rs;
    }

    public ValidateProfessionalLcRs validateProfessionalLicense(String newProfessionalLicense) {
        ValidateProfessionalLcRs rs = new ValidateProfessionalLcRs();
        Optional<UserProfileJpaEntity> userProfileOpt = userProfileJpaRepository.findByProfessionalLicense(newProfessionalLicense);
        if (userProfileOpt.isPresent()) {
            log.warn("validateProfessionalLicense() ... {}", ErrorCode.UM0015);
            rs.setAvailable(false);
            rs.setMessage(ErrorCode.UM0015.toString());
            return rs;
        }
        try {
            this.validateProfessionalLicenseFormat(newProfessionalLicense);
        } catch (Exception ex) {
            log.warn("validateProfessionalLicense() ... {}", ex.getMessage());
            rs.setAvailable(false);
            rs.setMessage(ex.getMessage());
            return rs;
        }
        rs.setAvailable(true);
        rs.setMessage("professional license is available");
        return rs;
    }

    public List<SearchUserRs> searchUser(SearchUserRq rq) {
        List<UserProfileSearchJdbcEntity> userSearchResults = userJdbcRepository.searchUserByCriteria(
                rq.getFirstName(),
                rq.getLastName(),
                rq.getEmail(),
                rq.getMobile(),
                rq.getProfessionalLicense(),
                rq.getLevel());
        log.info("searchUser() ... userSearchResults.size() = {}", userSearchResults.size());

        List<SearchUserRs> searchUserRsList = new ArrayList<>();
        for (UserProfileSearchJdbcEntity user : userSearchResults) {
            SearchUserRs rs = new SearchUserRs();

            if (user.getUserProfileId() == null) {
                log.warn("searchUser() ... found some user that's ID is undefined");
                continue;
            }

            rs.setUserProfileId(user.getUserProfileId());
            rs.setFirstName(user.getFirstName() == null ? "" : user.getFirstName());
            rs.setLastName(user.getLastName() == null ? "" : user.getLastName());
            rs.setEmail(user.getEmail() == null ? "" : user.getEmail());
            rs.setMobile(user.getMobile() == null ? "" : user.getMobile());
            rs.setProfessionalLicense(user.getProfessionalLicense() == null ? "" : user.getProfessionalLicense());
            rs.setLevel(user.getLevel() == null ? "" : user.getLevel());
            rs.setCreateBy(user.getCreateBy() == null ? "" : user.getCreateBy());
            rs.setUpdateDatetime(user.getUpdateDatetime() == null ? "" : dateFormat.format(user.getUpdateDatetime()));

            searchUserRsList.add(rs);
        }
        log.info("searchUser() ... userSearchRsList.size() = {}", searchUserRsList.size());

        userSearchResults.sort(Comparator.comparing(UserProfileSearchJdbcEntity::getFirstName));
        return searchUserRsList;
    }

    @Transactional
    public AddUserRs addUser(BigInteger makerId,
                             AddUserRq rq) {

        this.validateUsernameFormat(rq.getUsername());
        this.validatePasswordFormat(rq.getPassword());
        this.validateFirstNameFormat(rq.getFirstName());
        this.validateLastNameFormat(rq.getLastName());
        this.validateEmailFormat(rq.getEmail());
        this.validateMobileFormat(rq.getMobile());
        this.validateProfessionalLicenseFormat(rq.getProfessionalLicense());
        this.validateLevelFormat(rq.getLevel());

        if (ObjectUtils.isEmpty(rq.getUsername()) && ObjectUtils.isEmpty(rq.getProfessionalLicense())) {
            log.error("addUser() ... {}", ErrorCode.UM0011);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0011);
        }

        Optional<UserAuthJpaEntity> userAuthOpt = userAuthJpaRepository.findByUsername(rq.getUsername());
        if (userAuthOpt.isPresent()) {
            log.warn("addUser() ... {}", ErrorCode.UM0014);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0014);
        }
        Optional<UserProfileJpaEntity> usernameProfileOpt = userProfileJpaRepository.findByProfessionalLicense(rq.getUsername());
        if (usernameProfileOpt.isPresent()) {
            log.warn("addUser() ... {}", ErrorCode.UM0016);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0016);
        }
        Optional<UserProfileJpaEntity> profProfileOpt = userProfileJpaRepository.findByProfessionalLicense(rq.getProfessionalLicense());
        if (profProfileOpt.isPresent()) {
            log.error("addUser() ... {}", ErrorCode.UM0015);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0015);
        }

        Date currentDate = new Date();
        try {
            UserProfileJpaEntity profile = new UserProfileJpaEntity();
            if (rq.getFirstName() != null) {
                profile.setFirstName(rq.getFirstName());
            } else {
                log.error("addUser() ... {}", ErrorCode.UM0012);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0012);
            }
            if (rq.getLastName() != null) {
                profile.setLastName(rq.getLastName());
            } else {
                log.error("addUser() ... {}", ErrorCode.UM0013);
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0013);
            }
            if (rq.getEmail() != null) profile.setEmail(rq.getEmail());
            if (rq.getMobile() != null) profile.setMobile(rq.getMobile());
            if (rq.getProfessionalLicense() != null) profile.setProfessionalLicense(rq.getProfessionalLicense());
            profile.setCreateBy(makerId);
            profile.setCreatedDatetime(currentDate);
            profile.setUpdateBy(makerId);
            UserProfileJpaEntity savedProfile = userProfileJpaRepository.save(profile);

            UserAuthJpaEntity auth = new UserAuthJpaEntity();
            auth.setUserProfileId(savedProfile.getId());
            auth.setUsername(rq.getUsername() == null ? savedProfile.getProfessionalLicense() : rq.getUsername());
            auth.setPassword(rq.getPassword() == null ? credentialService.generateDefaultPassword(auth.getUsername()) : rq.getPassword());
            auth.setToken(null);
            auth.setLevel(rq.getLevel() == null ? UserLevel.MEMBER.toString() : rq.getLevel());
            auth.setActive(true);
            auth.setCreateBy(makerId);
            auth.setCreatedDatetime(currentDate);
            auth.setUpdateBy(makerId);
            UserAuthJpaEntity saveAuth = userAuthJpaRepository.save(auth);

            AddUserRs rs = new AddUserRs();
            rs.setFirstName(profile.getFirstName());
            rs.setLastName(profile.getLastName());
            rs.setEmail(profile.getEmail());
            rs.setMobile(profile.getMobile());
            rs.setProfessionalLicense(profile.getProfessionalLicense());
            rs.setUsername(saveAuth.getUsername());
            rs.setLevel(saveAuth.getLevel());

            return rs;
        } catch (Exception ex) {
            log.error("addUser() ... {} / message: {}", ErrorCode.DB0002, ex.getMessage());
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.DB0002);
        }
    }

    @Transactional
    public EditUserRs editUserByForm(BigInteger makerId,
                                     EditUserRq rq) {
        this.validateUsernameFormat(rq.getUsername());
        this.validatePasswordFormat(rq.getPassword());
        this.validateFirstNameFormat(rq.getFirstName());
        this.validateLastNameFormat(rq.getLastName());
        this.validateEmailFormat(rq.getEmail());
        this.validateMobileFormat(rq.getMobile());
        this.validateProfessionalLicenseFormat(rq.getProfessionalLicense());
        this.validateLevelFormat(rq.getLevel());

        Optional<UserProfileJpaEntity> existProfileOpt = userProfileJpaRepository.findById(rq.getUserProfileId());
        if (existProfileOpt.isEmpty()) {
            log.error("editUserByForm() ... {}", ErrorCode.UM0001);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0001);
        }

        UserProfileJpaEntity existProfile = existProfileOpt.get();

        Optional<UserAuthJpaEntity> existAuthOpt = userAuthJpaRepository.findByUserProfileId(existProfile.getId());
        if (existAuthOpt.isEmpty()) {
            log.error("editUserByForm() ... {}", ErrorCode.UM0002);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0002);
        }

        UserAuthJpaEntity existAuth = existAuthOpt.get();

        Date currentDate = new Date();
        EditUserRs rs = new EditUserRs();
        try {
            boolean isProfileChanged = false;
            if (!ObjectUtils.isEmpty(rq.getFirstName()) && !existProfile.getFirstName().equals(rq.getFirstName())) {
                existProfile.setFirstName(rq.getFirstName());
                isProfileChanged = true;
            }
            if (!ObjectUtils.isEmpty(rq.getLastName()) && !existProfile.getLastName().equals(rq.getLastName())) {
                existProfile.setLastName(rq.getLastName());
                isProfileChanged = true;
            }
            if (!ObjectUtils.isEmpty(rq.getEmail()) && !existProfile.getEmail().equals(rq.getEmail())) {
                existProfile.setEmail(rq.getEmail());
                isProfileChanged = true;
            }
            if (!ObjectUtils.isEmpty(rq.getMobile()) && !existProfile.getMobile().equals(rq.getMobile())) {
                existProfile.setMobile(rq.getMobile());
                isProfileChanged = true;
            }
            if (!ObjectUtils.isEmpty(rq.getProfessionalLicense()) && !existProfile.getProfessionalLicense().equals(rq.getProfessionalLicense())) {
                existProfile.setProfessionalLicense(rq.getProfessionalLicense());
                isProfileChanged = true;
            }
            if (isProfileChanged) {
                existProfile.setUpdateBy(makerId);
                existProfile.setUpdatedDatetime(currentDate);
            }

            UserProfileJpaEntity savedProfile = userProfileJpaRepository.save(existProfile);

            boolean isAuthChanged = false;
            if (!ObjectUtils.isEmpty(rq.getUsername()) && !existAuth.getUsername().equals(rq.getUsername())) {
                existAuth.setUsername(rq.getUsername());
                isAuthChanged = true;
            }
            if (!ObjectUtils.isEmpty(rq.getPassword()) && !existAuth.getPassword().equals(rq.getPassword())) {
                existAuth.setPassword(rq.getPassword());
                isAuthChanged = true;
            }
            if (existAuth.isActive() != rq.isActive()) {
                existAuth.setActive(rq.isActive());
                isAuthChanged = true;
            }
            if (!ObjectUtils.isEmpty(rq.getLevel()) && !existAuth.getLevel().equals(rq.getLevel())) {
                existAuth.setLevel(rq.getLevel());
                isAuthChanged = true;
            }
            if (isAuthChanged) {
                existAuth.setUpdateBy(makerId);
                existAuth.setUpdatedDatetime(currentDate);
            }

            UserAuthJpaEntity saveAuth = userAuthJpaRepository.save(existAuth);

            rs.setFirstName(savedProfile.getFirstName());
            rs.setLastName(savedProfile.getLastName());
            rs.setEmail(savedProfile.getEmail());
            rs.setMobile(savedProfile.getMobile());
            rs.setProfessionalLicense(savedProfile.getProfessionalLicense());
            rs.setUsername(saveAuth.getUsername());
            rs.setPassword(StringUtils.leftPad("", saveAuth.getPassword().length(), "*"));
            rs.setLevel(saveAuth.getLevel());

            return rs;
        } catch (Exception ex) {
            log.error("editUserByForm() ... {} / message: {}", ErrorCode.DB0003, ex.getMessage());
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.DB0003);
        }
    }

    @Transactional
    public void deleteUser(DeleteUserRq rq) {
        Optional<UserProfileJpaEntity> existProfileOpt = userProfileJpaRepository.findById(rq.getDeleteUserID());
        if (existProfileOpt.isEmpty()) {
            log.error("deleteUser() ... {}", ErrorCode.UM0001);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0001);
        }

        UserProfileJpaEntity existProfile = existProfileOpt.get();

        Optional<UserAuthJpaEntity> existAuthOpt = userAuthJpaRepository.findByUserProfileId(existProfile.getId());
        if (existAuthOpt.isEmpty()) {
            log.error("deleteUser() ... {}", ErrorCode.UM0002);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.UM0002);
        }

        UserAuthJpaEntity existAuth = existAuthOpt.get();

        try {
            log.info("deleteUser() ... deleting user authentication: {}", existAuth);
            userAuthJpaRepository.delete(existAuth);
            log.info("deleteUser() ... deleting user profile: {}", existProfile);
            userProfileJpaRepository.delete(existProfile);
        } catch (Exception ex) {
            log.error("deleteUser() ... {} / message: {}", ErrorCode.DB0004, ex.getMessage());
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DB0004);
        }
    }
}
