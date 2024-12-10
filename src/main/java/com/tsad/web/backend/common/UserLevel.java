package com.tsad.web.backend.common;

public enum UserLevel {
    MEMBER("MEMBER"),
    ADMIN("ADMIN"),
    MASTER("MASTER");

    private final String level;


    UserLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return this.level;
    }
}
