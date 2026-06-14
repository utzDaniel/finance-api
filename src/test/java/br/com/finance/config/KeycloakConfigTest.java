package br.com.finance.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Testes unitários de KeycloakConfig")
class KeycloakConfigTest {

    private KeycloakConfig keycloakConfig;

    @BeforeEach
    void setUp() {
        keycloakConfig = new KeycloakConfig();
        keycloakConfig.setUrl("http://localhost:9999");
        keycloakConfig.setRealm("development");
    }

    @Test
    @DisplayName("Deve criar RestClient bean")
    void deveCriarRestClientBean() {
        // Act
        RestClient restClient = keycloakConfig.keycloakAdminRestClient();

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @DisplayName("Deve retornar URL do JWK Set corretamente")
    void deveRetornarUrlDoJwkSetCorretamente() {
        // Act
        String jwkSetUri = keycloakConfig.getJwkSetUri();

        // Assert
        assertEquals("http://localhost:9999/realms/development/protocol/openid-connect/certs", jwkSetUri);
    }

    @Test
    @DisplayName("Deve permitir configuração de URL")
    void devePermitirConfiguracaoDeUrl() {
        // Arrange
        String novaUrl = "http://keycloak.exemplo.com";

        // Act
        keycloakConfig.setUrl(novaUrl);

        // Assert
        assertEquals(novaUrl, keycloakConfig.getUrl());
    }

    @Test
    @DisplayName("Deve permitir configuração de realm")
    void devePermitirConfiguracaoDeRealm() {
        // Arrange
        String novoRealm = "production";

        // Act
        keycloakConfig.setRealm(novoRealm);

        // Assert
        assertEquals(novoRealm, keycloakConfig.getRealm());
    }
}

