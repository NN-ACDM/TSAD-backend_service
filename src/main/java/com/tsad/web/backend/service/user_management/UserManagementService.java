package com.tsad.web.backend.service.user_management;

import com.tsad.web.backend.common.DateFormat;
import com.tsad.web.backend.common.UserLevel;
import com.tsad.web.backend.controller.user_management.model.AddUserProfileRq;
import com.tsad.web.backend.controller.user_management.model.UserSearchRq;
import com.tsad.web.backend.controller.user_management.model.UserSearchRs;
import com.tsad.web.backend.repository.webservicedb.jdbc.UserJdbcRepository;
import com.tsad.web.backend.repository.webservicedb.jdbc.model.UserProfileSearchJdbcEntity;
import com.tsad.web.backend.repository.webservicedb.jpa.UserAuthJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.UserProfileJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserProfileJpaEntity;
import com.tsad.web.backend.service.authentication.CredentialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat.DATETIME.toString());

    public List<UserSearchRs> searchUser(UserSearchRq rq) {
        log.info("searchUser() ... start");
        List<UserProfileSearchJdbcEntity> userSearchResults = userJdbcRepository.searchUserByCriteria(
                rq.getFirstName(),
                rq.getLastName(),
                rq.getEmail(),
                rq.getMobile(),
                rq.getProfessionalLicense());
        log.info("searchUser() ... userSearchResults.size() = {}", userSearchResults.size());

        List<UserSearchRs> userSearchRsList = new ArrayList<>();
        for (UserProfileSearchJdbcEntity user : userSearchResults) {
            UserSearchRs rs = new UserSearchRs();

            if (user.getUserProfileId() == null) {
                log.warn("searchUser() ... found some user that's ID is undefined");
                continue;
            }

            rs.setUserProfileId(user.getUserProfileId());
            rs.setFirstName(user.getFirstName() == null ? "" : user.getFirstName());
            rs.setLastName(user.getLastName() == null ? "" : user.getLastName());
            rs.setEmail(user.getEmail() == null ? "" : user.getEmail());
            rs.setMobile(user.getMobile() == null ? "" : user.getMobile());
            rs.setCreateBy(user.getCreateBy() == null ? "" : user.getCreateBy());
            rs.setUpdateDatetime(user.getUpdateDatetime() == null ? "" : dateFormat.format(user.getUpdateDatetime()));

            userSearchRsList.add(rs);
        }
        log.info("searchUser() ... userSearchRsList.size() = {}", userSearchRsList.size());

        userSearchResults.sort(Comparator.comparing(UserProfileSearchJdbcEntity::getFirstName));
        log.info("searchUser() ... finish");
        return userSearchRsList;
    }

    @Transactional
    public void addUserByRegisterForm(BigInteger makerId,
                                      AddUserProfileRq rq) {
        Date currentDate = new Date();

        UserProfileJpaEntity profile = new UserProfileJpaEntity();
        profile.setFirstName(rq.getFirstName());
        profile.setLastName(rq.getLastName());
        profile.setEmail(rq.getEmail());
        profile.setMobile(rq.getMobile());
        profile.setProfessionalLicense(rq.getProfessionalLicense());
        profile.setCreateBy(makerId);
        profile.setCreatedDatetime(currentDate);
        profile.setUpdateBy(makerId);
        UserProfileJpaEntity savedProfile = userProfileJpaRepository.save(profile);

        UserAuthJpaEntity auth = new UserAuthJpaEntity();
        auth.setUserProfileId(savedProfile.getId());
        auth.setUsername(this.getUsername(rq, savedProfile));
        auth.setPassword(this.getPassword(rq, auth));
        auth.setToken(null);
        auth.setLevel(this.getLevel(rq));
        auth.setCreateBy(makerId);
        auth.setCreatedDatetime(currentDate);
        auth.setUpdateBy(makerId);
    }

    private String getUsername(AddUserProfileRq rq, UserProfileJpaEntity savedProfile) {
        return ObjectUtils.isEmpty(rq.getUsername()) ? savedProfile.getProfessionalLicense() : rq.getUsername();
    }

    private String getPassword(AddUserProfileRq rq, UserAuthJpaEntity auth) {
        return ObjectUtils.isEmpty(rq.getPassword()) ? credentialService.generateDefaultPassword(auth.getUsername()) : rq.getPassword();
    }

    private String getLevel(AddUserProfileRq rq) {
        return ObjectUtils.isEmpty(rq.getLevel()) ? rq.getLevel() : UserLevel.MEMBER.toString();
    }
}
