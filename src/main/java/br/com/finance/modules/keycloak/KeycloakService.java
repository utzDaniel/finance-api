package br.com.finance.modules.keycloak;

import br.com.finance.config.ApiException;
import br.com.finance.config.KeycloakConfig;
import br.com.finance.modules.keycloak.dto.KeycloakUserFamilyRecord;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakService {

    private final KeycloakReadRepository keycloakReadRepository;
    private final KeycloakConfig keycloakConfig;

    public KeycloakService(KeycloakReadRepository keycloakReadRepository, KeycloakConfig keycloakConfig) {
        this.keycloakReadRepository = keycloakReadRepository;
        this.keycloakConfig = keycloakConfig;
    }

    public KeycloakUserFamilyRecord getUserFamily(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return keycloakReadRepository
                .findUserFamilyByRealmAndUsername(keycloakConfig.getRealm(), username)
                .orElseThrow(() -> ApiException.notFound("User não encontrado"));
    }

    public List<String> getFamilyUserIds(Long familyId) {
        return keycloakReadRepository.findUserIdsByFamilyId(familyId);
    }
}
