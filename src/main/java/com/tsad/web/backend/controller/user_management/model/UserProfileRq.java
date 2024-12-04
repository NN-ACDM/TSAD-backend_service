package com.tsad.web.backend.controller.user_management.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserProfileRq {
    @NotNull(message = "the first name is required")
    private String firstName;

    @NotNull(message = "the last name is required")
    private String lastName;

    @NotNull(message = "the email is required")
    private String email;

    @NotNull(message = "the mobile number is required")
    private String mobile;

    @NotNull(message = "the professional license is required")
    private String professionalLicense;

    private String username;
    private String password;
    private String level;
}
