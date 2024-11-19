package com.tsad.web.backend.repository.webservicedb.jpa;

import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface UserAuthJpaRepository extends JpaRepository<UserAuthJpaEntity, BigInteger> {
    UserAuthJpaEntity findByToken(String token);
    UserAuthJpaEntity findByUsername(String username);
}
