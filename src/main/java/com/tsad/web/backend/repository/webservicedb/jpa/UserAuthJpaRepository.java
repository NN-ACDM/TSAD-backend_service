package com.tsad.web.backend.repository.webservicedb.jpa;

import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserAuthJpaRepository extends JpaRepository<UserAuthJpaEntity, BigInteger> {
    Set<UserAuthJpaEntity> findByToken(String token);
    Optional<UserAuthJpaEntity> findByUsername(String username);
    Optional<UserAuthJpaEntity> findByUserProfileId(BigInteger id);
    Optional<UserAuthJpaEntity> findByUsernameAndToken(String username, String token);
    Optional<UserAuthJpaEntity> findByUsernameAndPassword(String username, String password);
}
