package br.com.finance.modules.keycloak;

import br.com.finance.modules.keycloak.dto.KeycloakUserFamilyRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class KeycloakReadRepository {

    private static final String USER_FAMILY_SQL = """
            SELECT
                u.ID,
                u.FIRST_NAME,
                u.LAST_NAME,
                m.FAMILY_ID,
                f.NAME
            FROM USER_ENTITY u
            INNER JOIN REALM r ON u.REALM_ID = r.ID
            LEFT JOIN FAMILY_MEMBER m ON u.ID = m.USER_ID
            LEFT JOIN FAMILY_ENTITY f ON m.FAMILY_ID = f.ID
            WHERE r.NAME = ? AND u.USERNAME = ?
            """;

    private static final String FAMILY_MEMBERS_SQL = """
            SELECT m.USER_ID
            FROM FAMILY_MEMBER m
            WHERE m.FAMILY_ID = ?
            """;

    private final JdbcTemplate keycloakJdbcTemplate;

    public KeycloakReadRepository(@Qualifier("keycloakJdbcTemplate") JdbcTemplate keycloakJdbcTemplate) {
        this.keycloakJdbcTemplate = keycloakJdbcTemplate;
    }

    public Optional<KeycloakUserFamilyRecord> findUserFamilyByRealmAndUsername(String realmName, String username) {
        List<KeycloakUserFamilyRecord> result = keycloakJdbcTemplate.query(
                USER_FAMILY_SQL,
                (rs, rowNum) -> new KeycloakUserFamilyRecord(
                        rs.getString("ID"),
                        rs.getString("FIRST_NAME"),
                        rs.getString("LAST_NAME"),
                        rs.getObject("FAMILY_ID", Long.class),
                        rs.getString("NAME")
                ),
                realmName,
                username
        );
        return result.stream().findFirst();
    }

    public List<String> findUserIdsByFamilyId(Long familyId) {
        return keycloakJdbcTemplate.query(
                FAMILY_MEMBERS_SQL,
                (rs, rowNum) -> rs.getString("USER_ID"),
                familyId
        );
    }
}