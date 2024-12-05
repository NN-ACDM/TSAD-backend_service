package com.tsad.web.backend.common;

public enum ErrorCode {
    // Credential
    CR0001("token is empty"),
    CR0002("username is empty"),
    CR0003("password is empty"),
    CR0004("token is invalid"),
    CR0005("username or password is invalid"),

    // Database
    DB0001("save token failed"),
    DB0002("save token(null) failed");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
