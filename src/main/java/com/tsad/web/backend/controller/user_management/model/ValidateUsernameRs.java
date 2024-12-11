package com.tsad.web.backend.controller.user_management.model;

import lombok.Data;

@Data
public class ValidateUsernameRs {
    private boolean isAvailable;
    private String message;
}
