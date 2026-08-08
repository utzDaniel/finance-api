package br.com.finance.modules.keycloak;

import br.com.finance.config.ApiException;
import br.com.finance.config.KeycloakConfig;
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

    public String getIdUser(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return keycloakReadRepository
                .findUserId(keycloakConfig.getRealm(), username)
                .orElseThrow(() -> ApiException.notFound("User não encontrado"));
    }

    public List<String> getIdUsers(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return keycloakReadRepository.findUsersId(keycloakConfig.getRealm(), username);
    }

}
