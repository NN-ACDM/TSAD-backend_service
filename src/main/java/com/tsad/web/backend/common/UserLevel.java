package com.tsad.web.backend.common;

public enum UserLevel {
    MEMBER("member"),
    ADMIN("admin"),
    MASTER_ADMIN("master-admin");

    private final String level;


    UserLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return this.level;
    }
}
