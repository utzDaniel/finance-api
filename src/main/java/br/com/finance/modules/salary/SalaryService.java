package br.com.finance.modules.salary;

import br.com.finance.modules.event.EventPublisher;
import br.com.finance.modules.keycloak.KeycloakService;
import br.com.finance.modules.keycloak.dto.KeycloakUserFamilyRecord;
import br.com.finance.modules.salary.dto.SalaryDetailResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SalaryService {

    private final KeycloakService keycloakService;
    private final EventPublisher eventPublisher;


    public SalaryService(KeycloakService keycloakService, EventPublisher eventPublisher) {
        this.keycloakService = keycloakService;
        this.eventPublisher = eventPublisher;
    }

    public SalaryDetailResponse getSalaryDetail(Jwt jwt, LocalDate competenceDate) {
        KeycloakUserFamilyRecord userFamily = keycloakService.getUserFamily(jwt);
        return null;
    }
}
