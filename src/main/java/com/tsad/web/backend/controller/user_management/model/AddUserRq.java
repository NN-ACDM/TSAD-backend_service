package com.tsad.web.backend.controller.user_management.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddUserRq {
    @NotEmpty(message = "the first name is required")
    private String firstName;

    @NotEmpty(message = "the last name is required")
    private String lastName;

    private String email;
    private String mobile;
    private String username;
    private String professionalLicense;
    private String password;
    private String level;
}
