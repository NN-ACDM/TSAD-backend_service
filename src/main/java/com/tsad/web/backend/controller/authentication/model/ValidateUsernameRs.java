package com.tsad.web.backend.controller.authentication.model;

import lombok.Data;

@Data
public class ValidateUsernameRs {
    private boolean isAvailable;
    private String message;
}
