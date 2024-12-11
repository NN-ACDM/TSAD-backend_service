package com.tsad.web.backend.controller.user_management.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigInteger;

@Data
public class EditUserRq {
    @NotNull(message = "the user ID is required")
    private BigInteger userProfileId;

    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String professionalLicense;
    private String username;
    private String password;
    private String level;
    private boolean isActive;
}
