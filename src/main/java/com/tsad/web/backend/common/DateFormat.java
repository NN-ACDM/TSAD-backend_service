package com.tsad.web.backend.common;

public enum DateFormat {
    TIME("hh:mm:ss"),
    DATE("yyyy-MM-dd"),
    DATETIME("yyyy-MM-dd hh:mm:ss");

    private final String format;

    DateFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return this.format;
    }
}
