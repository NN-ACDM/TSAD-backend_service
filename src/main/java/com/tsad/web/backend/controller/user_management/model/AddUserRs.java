package com.tsad.web.backend.controller.user_management.model;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AddUserRs {
    private BigInteger userProfileId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String professionalLicense;
    private String username;
    private String level;
}
