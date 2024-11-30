package com.tsad.web.backend.service.user_management;

import com.tsad.web.backend.auth.CredentialService;
import com.tsad.web.backend.common.UserLevel;
import com.tsad.web.backend.controller.user_management.model.UserProfileRq;
import com.tsad.web.backend.repository.webservicedb.jpa.UserAuthJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.UserProfileJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserProfileJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigInteger;
import java.util.Date;

@Service
public class UserManagementService {

    @Autowired
    private UserProfileJpaRepository userProfileJpaRepository;

    @Autowired
    private UserAuthJpaRepository userAuthJpaRepository;

    @Autowired
    private CredentialService credentialService;


    @Transactional
    public void addUser(BigInteger makerId, UserProfileRq rq) {
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

    private String getUsername(UserProfileRq rq, UserProfileJpaEntity savedProfile) {
        return ObjectUtils.isEmpty(rq.getUsername()) ? savedProfile.getProfessionalLicense() : rq.getUsername();
    }

    private String getPassword(UserProfileRq rq, UserAuthJpaEntity auth) {
        return ObjectUtils.isEmpty(rq.getPassword()) ? credentialService.generateDefaultPassword(auth.getUsername()) : rq.getPassword();
    }

    private String getLevel(UserProfileRq rq) {
        return ObjectUtils.isEmpty(rq.getLevel()) ? rq.getLevel() : UserLevel.MEMBER.toString();
    }

    public void addUserByRegisterForm() {

    }
}
