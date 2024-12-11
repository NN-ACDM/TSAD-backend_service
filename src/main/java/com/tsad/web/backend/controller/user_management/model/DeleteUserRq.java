package com.tsad.web.backend.controller.user_management.model;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DeleteUserRq {
    private BigInteger deleteUserID;
}
