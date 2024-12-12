package com.tsad.web.backend.repository.webservicedb.jdbc;

import com.tsad.web.backend.repository.webservicedb.jdbc.model.UserProfileSearchJdbcEntity;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserJdbcRepository {
    private static final Logger log = LoggerFactory.getLogger(UserJdbcRepository.class);

    @Qualifier(value = "webServiceDbJdbcTemplate")
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserJdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<UserProfileSearchJdbcEntity> searchUserByCriteria(String firstName,
                                                                  String lastName,
                                                                  String email,
                                                                  String mobile,
                                                                  String professionalLicense,
                                                                  String level) {

        String sql = """
                 SELECT up.id AS userProfileId,
                 up.first_name AS firstName,
                 up.last_name AS lastName,
                 up.email AS email,
                 up.mobile AS mobile,
                 up.professional_license AS professionalLicense,
                 ua.level AS level,
                 creator.first_name AS createBy,
                 up.updated_datetime AS updateDatetime
                 FROM user_profile up
                 INNER JOIN user_auth ua ON ua.user_profile_id = up.id
                 LEFT JOIN user_profile creator ON creator.id = up.create_by
                 WHERE 1 = 1\
                """;

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        if (StringUtils.isNotEmpty(firstName)) {
            mapSqlParameterSource.addValue("firstName", "%" + firstName + "%");
            sql += " AND up.first_name LIKE :firstName\n";
        }

        if (StringUtils.isNotEmpty(lastName)) {
            mapSqlParameterSource.addValue("lastName", "%" + lastName + "%");
            sql += " AND up.last_name LIKE :lastName\n";
        }

        if (StringUtils.isNotEmpty(email)) {
            mapSqlParameterSource.addValue("email", email);
            sql += " AND up.email = :email\n";
        }

        if (StringUtils.isNotEmpty(mobile)) {
            mapSqlParameterSource.addValue("mobile", mobile);
            sql += " AND up.mobile = :mobile\n";
        }

        if (StringUtils.isNotEmpty(professionalLicense)) {
            mapSqlParameterSource.addValue("professionalLicense", professionalLicense);
            sql += " AND up.professional_license = :professionalLicense\n";
        }

        if (StringUtils.isNotEmpty(level)) {
            mapSqlParameterSource.addValue("level", level);
            sql += " AND ua.level = :level\n";
        }
        log.debug("searchUserByCriteria() ... SQL Statement: ");
        return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(UserProfileSearchJdbcEntity.class));
    }
}
