package com.tsad.web.backend.controller.user_management.model;

import lombok.Data;

@Data
public class SearchUserRq {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String professionalLicense;
    private String level;
}
