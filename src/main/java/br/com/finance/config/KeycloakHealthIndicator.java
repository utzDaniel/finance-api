package br.com.finance.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("keycloak")
public class KeycloakHealthIndicator extends AbstractHealthIndicator {

    private final String issuerUri;
    private final RestClient restClient;

    @Autowired
    public KeycloakHealthIndicator(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
        this.issuerUri = issuerUri;
        this.restClient = RestClient.create();
    }

    KeycloakHealthIndicator(String issuerUri, RestClient restClient) {
        this.issuerUri = issuerUri;
        this.restClient = restClient;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            restClient.get()
                .uri(issuerUri + "/.well-known/openid-configuration")
                .retrieve()
                .toBodilessEntity();
            builder.up().withDetail("issuer", issuerUri);
        } catch (Exception e) {
            builder.down()
                .withDetail("issuer", issuerUri)
                .withDetail("error", e.getMessage());
        }
    }
}
