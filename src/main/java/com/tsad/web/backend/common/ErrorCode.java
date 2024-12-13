package com.tsad.web.backend.common;

public enum ErrorCode {
    // Credential
    CR0001("token is empty"),
    CR0002("username is empty"),
    CR0003("password is empty"),
    CR0004("token is invalid or expired"),
    CR0005("username or password is invalid"),
    CR0006("user ID should not be NULL"),
    CR0007("user is inactive"),
    CR0008("current password is invalid"),

    // Database
    DB0001("save token failed"),
    DB0002("add user failed"),
    DB0003("edit user failed"),
    DB0004("delete user failed"),

    // User management
    UM0001("user profile not found"),
    UM0002("user authentication not found"),
    UM0003("invalid username"),
    UM0004("invalid password"),
    UM0005("invalid first name"),
    UM0006("invalid last name"),
    UM0007("invalid email"),
    UM0008("invalid mobile"),
    UM0009("invalid professional license"),
    UM0010("invalid level"),
    UM0011("invalid user's adding request"),
    UM0012("first name must not be empty"),
    UM0013("last name must not be empty"),
    UM0014("duplicate username"),
    UM0015("duplicate professional license"),
    UM0016("username is duplicate in professional license"),
    ;

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
