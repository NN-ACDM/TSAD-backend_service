package com.tsad.web.backend.repository.webservicedb.jpa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "user_auth")
@Data
public class UserAuthJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger id;

    @Column(name = "user_profile_id")
    private BigInteger userProfileId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "token")
    private String token;

    @Column(name = "level")
    private String level;

    @Column(name = "create_by")
    private BigInteger createBy;

    @Column(name = "created_datetime")
    private Date createdDatetime;

    @Column(name = "update_by")
    private BigInteger updateBy;

    @Column(name = "updated_datetime")
    private Date updatedDatetime;
}
