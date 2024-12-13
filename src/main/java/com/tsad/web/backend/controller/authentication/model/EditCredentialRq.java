package com.tsad.web.backend.controller.authentication.model;

import lombok.Data;

@Data
public class EditCredentialRq {
    private String username;
    private String password;
    private String currentPassword;
}
