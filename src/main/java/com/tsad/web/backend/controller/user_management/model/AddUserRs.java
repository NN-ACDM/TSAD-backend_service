package com.tsad.web.backend.controller.user_management.model;

import lombok.Data;

@Data
public class AddUserRs {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String professionalLicense;
    private Boolean isActive;
    private String username;
    private String level;
}
