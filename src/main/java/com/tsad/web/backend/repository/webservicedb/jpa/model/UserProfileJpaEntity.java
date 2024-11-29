package com.tsad.web.backend.repository.webservicedb.jpa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "user_profile")
@Data
public class UserProfileJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "professional_license")
    private String professionalLicense;

    @Column(name = "create_by")
    private BigInteger createBy;

    @Column(name = "created_datetime")
    private Date createdDatetime;

    @Column(name = "update_by")
    private BigInteger updateBy;

    @Column(name = "updated_datetime")
    private Date updatedDatetime;
}
