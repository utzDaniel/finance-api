package br.com.finance.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KeycloakHealthIndicatorTest {

    private static final String ISSUER_URI = "http://localhost:8081/realms/finance-dev";

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void doHealthCheckShouldReturnUpWhenKeycloakIsAccessible() {
        RestClient restClient = buildMockRestClient(false);
        KeycloakHealthIndicator indicator = new KeycloakHealthIndicator(ISSUER_URI, restClient);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(ISSUER_URI, health.getDetails().get("issuer"));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void doHealthCheckShouldReturnDownWhenKeycloakIsNotAccessible() {
        RestClient restClient = buildMockRestClient(true);
        KeycloakHealthIndicator indicator = new KeycloakHealthIndicator(ISSUER_URI, restClient);

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(ISSUER_URI, health.getDetails().get("issuer"));
        assertNotNull(health.getDetails().get("error"));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private RestClient buildMockRestClient(boolean throwError) {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        if (throwError) {
            when(responseSpec.toBodilessEntity())
                .thenThrow(new RuntimeException("Connection refused: connect to localhost:8081 failed"));
        } else {
            when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());
        }

        return restClient;
    }
}
