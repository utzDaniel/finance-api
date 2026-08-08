package br.com.finance.modules.keycloak;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class KeycloakReadRepository {

    private static final String USER_FAMILY_SQL = """
            WITH UserFamily AS (
                SELECT
                    u.ID AS USER_ID,
                    fm.FAMILY_ID
                FROM USER_ENTITY u
                INNER JOIN REALM r
                    ON r.ID = u.REALM_ID
                LEFT JOIN FAMILY_MEMBER fm
                    ON fm.USER_ID = u.ID
                WHERE r.NAME = ?
                  AND u.USERNAME = ?
            )
            SELECT DISTINCT
                u.ID
            FROM USER_ENTITY u
            INNER JOIN REALM r
                ON r.ID = u.REALM_ID
            LEFT JOIN FAMILY_MEMBER fm
                ON fm.USER_ID = u.ID
            LEFT JOIN FAMILY_ENTITY f
                ON f.ID = fm.FAMILY_ID
            CROSS JOIN UserFamily uf
            WHERE r.NAME = ?
              AND (
                  (
                      uf.FAMILY_ID IS NULL
                      AND u.ID = uf.USER_ID
                  )
                  OR
                  (
                      uf.FAMILY_ID IS NOT NULL
                      AND fm.FAMILY_ID = uf.FAMILY_ID
                  )
              )
            """;

    private static final String USER_SQL = """
            SELECT
                u.ID
            FROM USER_ENTITY u
            INNER JOIN REALM r ON u.REALM_ID = r.ID
            WHERE r.NAME = ? AND u.USERNAME = ?
            """;

    private final JdbcTemplate keycloakJdbcTemplate;

    public KeycloakReadRepository(@Qualifier("keycloakJdbcTemplate") JdbcTemplate keycloakJdbcTemplate) {
        this.keycloakJdbcTemplate = keycloakJdbcTemplate;
    }

    public Optional<String> findUserId(String realmName, String username) {
        return keycloakJdbcTemplate.query(
                USER_SQL, (rs, rowNum) -> rs.getString("ID"),
                realmName,
                username
        ).stream().findFirst();
    }

    public List<String> findUsersId(String realmName, String username) {
        return keycloakJdbcTemplate.query(
                USER_FAMILY_SQL, (rs, rowNum) -> rs.getString("ID"),
                realmName,
                username,
                realmName
        ).stream().toList();
    }

}