package com.tsad.web.backend.repository.webservicedb.jdbc.model;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class UserProfileSearchJdbcEntity {
    private BigInteger userProfileId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String professionalLicense;
    private String createBy;
    private Date updateDatetime;
}
