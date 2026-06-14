package br.com.finance.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakConfig {

    private String url;
    private String realm;

    @Bean
    public RestClient keycloakAdminRestClient() {
        return RestClient.builder()
                .baseUrl(url)
                .build();
    }

    public String getJwkSetUri() {
        return String.format("%s/realms/%s/protocol/openid-connect/certs", url, realm);
    }

}

