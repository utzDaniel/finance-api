package br.com.finance.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import(SecurityConfig.class)
@TestPropertySource(properties = "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://mock-keycloak/realms/development")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void swaggerUiShouldBeAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()))
            .andExpect(result -> assertNotEquals(403, result.getResponse().getStatus()));
    }

    @Test
    void actuatorHealthShouldBeAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()))
            .andExpect(result -> assertNotEquals(403, result.getResponse().getStatus()));
    }

    @Test
    void actuatorInfoShouldBeAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/actuator/info"))
            .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()))
            .andExpect(result -> assertNotEquals(403, result.getResponse().getStatus()));
    }

    @Test
    void actuatorMetricsShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.error").value("Unauthorized"))
            .andExpect(jsonPath("$.message").value("Token ausente ou inválido"));
    }

    @Test
    void getFinancesShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/finances"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void getFinancesShouldReturn403WithTokenButWithoutScope() throws Exception {
        mockMvc.perform(get("/api/v1/finances")
                .with(jwt()))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void getFinancesShouldReturn403WithWrongScope() throws Exception {
        mockMvc.perform(get("/api/v1/finances")
                .with(jwt().authorities(new SimpleGrantedAuthority("finance.write"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void getFinancesShouldBeAccessibleWithReadScope() throws Exception {
        mockMvc.perform(get("/api/v1/finances")
                .with(jwt().authorities(new SimpleGrantedAuthority("finance.read"))))
            .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()))
            .andExpect(result -> assertNotEquals(403, result.getResponse().getStatus()));
    }

    @Test
    void postFinancesShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/finances")
                .contentType("application/json")
                .content("{}"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void postFinancesShouldReturn403WithReadScope() throws Exception {
        mockMvc.perform(post("/api/v1/finances")
                .contentType("application/json")
                .content("{}")
                .with(jwt().authorities(new SimpleGrantedAuthority("finance.read"))))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void postFinancesShouldBeAccessibleWithWriteScope() throws Exception {
        mockMvc.perform(post("/api/v1/finances")
                .contentType("application/json")
                .content("{}")
                .with(jwt().authorities(new SimpleGrantedAuthority("finance.write"))))
            .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()))
            .andExpect(result -> assertNotEquals(403, result.getResponse().getStatus()));
    }

    @Test
    void putFinanceShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(put("/api/v1/finances/1")
                .contentType("application/json")
                .content("{}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void putFinanceShouldReturn403WithReadScope() throws Exception {
        mockMvc.perform(put("/api/v1/finances/1")
                .contentType("application/json")
                .content("{}")
                .with(jwt().authorities(new SimpleGrantedAuthority("finance.read"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void putFinanceShouldBeAccessibleWithWriteScope() throws Exception {
        mockMvc.perform(put("/api/v1/finances/1")
                .contentType("application/json")
                .content("{}")
                .with(jwt().authorities(new SimpleGrantedAuthority("finance.write"))))
            .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()))
            .andExpect(result -> assertNotEquals(403, result.getResponse().getStatus()));
    }

    @Test
    void deleteFinanceShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(delete("/api/v1/finances/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteFinanceShouldReturn403WithReadScope() throws Exception {
        mockMvc.perform(delete("/api/v1/finances/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("finance.read"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void deleteFinanceShouldBeAccessibleWithWriteScope() throws Exception {
        mockMvc.perform(delete("/api/v1/finances/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("finance.write"))))
            .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()))
            .andExpect(result -> assertNotEquals(403, result.getResponse().getStatus()));
    }
}
