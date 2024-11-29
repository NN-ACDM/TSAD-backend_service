package com.tsad.web.backend.repository.webservicedb.jdbc.model;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class UserProfileJdbcEntity {
    private BigInteger id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String professionalLicense;
    private BigInteger createBy;
    private Date createdDatetime;
    private BigInteger updateBy;
    private Date updatedDatetime;
}
