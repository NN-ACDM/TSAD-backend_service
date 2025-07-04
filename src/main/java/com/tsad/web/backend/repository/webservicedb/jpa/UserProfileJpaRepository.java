package com.tsad.web.backend.repository.webservicedb.jpa;

import com.tsad.web.backend.repository.webservicedb.jpa.model.UserProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserProfileJpaRepository extends JpaRepository<UserProfileJpaEntity, BigInteger> {
    Optional<UserProfileJpaEntity> findByProfessionalLicense(String professionalLicense);
}
