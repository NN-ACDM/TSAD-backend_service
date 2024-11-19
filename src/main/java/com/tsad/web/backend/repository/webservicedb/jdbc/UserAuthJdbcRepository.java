package com.tsad.web.backend.repository.webservicedb.jdbc;

import com.tsad.web.backend.repository.webservicedb.jdbc.model.UserAuthJdbcEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserAuthJdbcRepository {

    @Autowired
    @Qualifier(value = "webServiceDbJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<UserAuthJdbcEntity> getEntitiesByUsernameAndPassword(String username, String password) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("username", username);
        mapSqlParameterSource.addValue("password", password);

        String sql = " SELECT ua.username as username, " +
                        " ua.password as password, " +
                        " ua.token as token " +
                        " FROM user_auth ua " +
                        " WHERE ua.username = :username " +
                        " AND ua.password = :password ";

        return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(UserAuthJdbcEntity.class));
    }
}
