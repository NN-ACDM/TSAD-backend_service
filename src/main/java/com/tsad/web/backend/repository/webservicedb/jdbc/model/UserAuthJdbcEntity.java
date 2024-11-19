package com.tsad.web.backend.repository.webservicedb.jdbc.model;

import lombok.Data;

@Data
public class UserAuthJdbcEntity {
    private String username;
    private String password;
    private String token;
}
