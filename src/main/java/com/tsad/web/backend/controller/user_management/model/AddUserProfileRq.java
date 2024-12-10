package com.tsad.web.backend.controller.user_management.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddUserProfileRq {
    @NotEmpty(message = "the first name is required")
    private String firstName;

    @NotEmpty(message = "the last name is required")
    private String lastName;

    @NotEmpty(message = "the email is required")
    private String email;

    @NotEmpty(message = "the mobile number is required")
    private String mobile;

    @NotEmpty(message = "the professional license is required")
    private String professionalLicense;

    private String username;
    private String password;
    private String level;
}
