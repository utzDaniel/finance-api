package br.com.finance.modules.summary;

import br.com.finance.config.ApiException;
import br.com.finance.config.KeycloakConfig;
import br.com.finance.modules.event.EventPublisher;
import br.com.finance.modules.event.EventType;
import br.com.finance.modules.keycloak.KeycloakReadRepository;
import br.com.finance.modules.keycloak.dto.KeycloakUserFamilyRecord;
import br.com.finance.modules.summary.dto.MonthlySummaryResponse;
import br.com.finance.modules.summary.dto.SalaryEntity;
import br.com.finance.modules.summary.dto.SumSalaryDto;
import br.com.finance.modules.summary.dto.UpdateMonthlySummaryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalaryService - Testes")
class SummaryServiceTest {

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private KeycloakReadRepository keycloakReadRepository;

    @Mock
    private KeycloakConfig keycloakConfig;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private SummaryService salaryService;

    @Test
    @DisplayName("getMonthlySummary - deve lançar not found quando usuário não existe no Keycloak")
    void getMonthlySummaryDeveLancarNotFoundQuandoUsuarioNaoExisteNoKeycloak() {
        Jwt jwt = buildJwt("john.doe");
        LocalDate competenceDate = LocalDate.of(2026, 6, 1);

        when(keycloakConfig.getRealm()).thenReturn("finance");
        when(keycloakReadRepository.findUserFamilyByRealmAndUsername("finance", "john.doe"))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> salaryService.getMonthlySummary(jwt, competenceDate));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("getMonthlySummary - deve retornar resumo sem família quando usuário não tem familyId")
    void getMonthlySummaryDeveRetornarResumoSemFamiliaQuandoUsuarioNaoTemFamilyId() {
        Jwt jwt = buildJwt("john.doe");
        LocalDate competenceDate = LocalDate.of(2026, 6, 1);
        KeycloakUserFamilyRecord userFamily = new KeycloakUserFamilyRecord("u-1", "John", "Doe", null, null);

        SalaryEntity salaryEntity = new SalaryEntity();
        salaryEntity.setId(10L);
        salaryEntity.setUserId("u-1");
        salaryEntity.setCompetenceDate(competenceDate);
        salaryEntity.setGrossSalary(new BigDecimal("1234.5"));
        salaryEntity.setNetSalary(new BigDecimal("987.1"));

        when(keycloakConfig.getRealm()).thenReturn("finance");
        when(keycloakReadRepository.findUserFamilyByRealmAndUsername("finance", "john.doe"))
                .thenReturn(Optional.of(userFamily));
        when(salaryRepository.findByUserIdAndCompetenceDate("u-1", competenceDate))
                .thenReturn(Optional.of(salaryEntity));

        MonthlySummaryResponse response = salaryService.getMonthlySummary(jwt, competenceDate);

        assertEquals("John", response.nome());
        assertEquals("Doe", response.sobrenome());
        assertEquals(new BigDecimal("1234.50"), response.userGrossSalary());
        assertEquals(new BigDecimal("987.10"), response.userNetSalary());
        assertEquals(new BigDecimal("0.00"), response.familyGrossSalary());
        assertEquals(new BigDecimal("0.00"), response.familyNetSalary());
        verify(keycloakReadRepository, never()).findUserIdsByFamilyId(anyLong());
    }

    @Test
    @DisplayName("getMonthlySummary - deve agregar salário da família quando existir familyId")
    void getMonthlySummaryDeveAgregarSalarioDaFamiliaQuandoExistirFamilyId() {
        Jwt jwt = buildJwt("john.doe");
        LocalDate competenceDate = LocalDate.of(2026, 6, 1);
        KeycloakUserFamilyRecord userFamily = new KeycloakUserFamilyRecord("u-1", "John", "Doe", 99L, "Doe Family");

        SalaryEntity salaryEntity = new SalaryEntity();
        salaryEntity.setUserId("u-1");
        salaryEntity.setCompetenceDate(competenceDate);
        salaryEntity.setGrossSalary(new BigDecimal("3000"));
        salaryEntity.setNetSalary(new BigDecimal("2400"));

        SumSalaryDto sumSalaryDto = org.mockito.Mockito.mock(SumSalaryDto.class);
        when(sumSalaryDto.getGrossSalary()).thenReturn(new BigDecimal("5000.567"));
        when(sumSalaryDto.getNetSalary()).thenReturn(new BigDecimal("4200.123"));

        when(keycloakConfig.getRealm()).thenReturn("finance");
        when(keycloakReadRepository.findUserFamilyByRealmAndUsername("finance", "john.doe"))
                .thenReturn(Optional.of(userFamily));
        when(salaryRepository.findByUserIdAndCompetenceDate("u-1", competenceDate))
                .thenReturn(Optional.of(salaryEntity));
        when(keycloakReadRepository.findUserIdsByFamilyId(99L)).thenReturn(List.of("u-1", "u-2"));
        when(salaryRepository.sumSalaryByUserIdInAndCompetenceDate(List.of("u-1", "u-2"), competenceDate))
                .thenReturn(sumSalaryDto);

        MonthlySummaryResponse response = salaryService.getMonthlySummary(jwt, competenceDate);

        assertEquals("Doe Family", response.familyName());
        assertEquals(new BigDecimal("5000.57"), response.familyGrossSalary());
        assertEquals(new BigDecimal("4200.12"), response.familyNetSalary());
        assertEquals(new BigDecimal("3000.00"), response.userGrossSalary());
        assertEquals(new BigDecimal("2400.00"), response.userNetSalary());
    }

    @Test
    @DisplayName("updateMonthlySummary - deve rejeitar quando salário líquido for maior que o bruto")
    void updateMonthlySummaryDeveRejeitarQuandoSalarioLiquidoForMaiorQueBruto() {
        UpdateMonthlySummaryRequest request = new UpdateMonthlySummaryRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("1200.00")
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> salaryService.updateMonthlySummary(buildJwt("john.doe"), LocalDate.of(2026, 6, 1), request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.hasViolacoes());
        assertEquals(2, exception.getViolacoes().size());
        verifyNoInteractions(salaryRepository, keycloakReadRepository, keycloakConfig, eventPublisher);
    }

    @Test
    @DisplayName("updateMonthlySummary - deve salvar salário e publicar evento")
    void updateMonthlySummaryDeveSalvarSalarioEPublicarEvento() {
        Jwt jwt = buildJwt("john.doe");
        LocalDate competenceDate = LocalDate.of(2026, 6, 1);
        UpdateMonthlySummaryRequest request = new UpdateMonthlySummaryRequest(
                new BigDecimal("3200.5"),
                new BigDecimal("2500.25")
        );
        KeycloakUserFamilyRecord userFamily = new KeycloakUserFamilyRecord("u-1", "John", "Doe", 99L, "Doe Family");

        SumSalaryDto sumSalaryDto = org.mockito.Mockito.mock(SumSalaryDto.class);
        when(sumSalaryDto.getGrossSalary()).thenReturn(new BigDecimal("5000.00"));
        when(sumSalaryDto.getNetSalary()).thenReturn(new BigDecimal("4000.00"));

        when(keycloakConfig.getRealm()).thenReturn("finance");
        when(keycloakReadRepository.findUserFamilyByRealmAndUsername("finance", "john.doe"))
                .thenReturn(Optional.of(userFamily));
        when(salaryRepository.findByUserIdAndCompetenceDate("u-1", competenceDate))
                .thenReturn(Optional.empty());
        when(salaryRepository.save(any(SalaryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(keycloakReadRepository.findUserIdsByFamilyId(99L)).thenReturn(List.of("u-1", "u-2"));
        when(salaryRepository.sumSalaryByUserIdInAndCompetenceDate(List.of("u-1", "u-2"), competenceDate))
                .thenReturn(sumSalaryDto);

        MonthlySummaryResponse response = salaryService.updateMonthlySummary(jwt, competenceDate, request);

        ArgumentCaptor<SalaryEntity> captor = ArgumentCaptor.forClass(SalaryEntity.class);
        verify(salaryRepository).save(captor.capture());
        SalaryEntity savedEntity = captor.getValue();
        assertEquals("u-1", savedEntity.getUserId());
        assertEquals(competenceDate, savedEntity.getCompetenceDate());
        assertEquals(new BigDecimal("3200.5"), savedEntity.getGrossSalary());
        assertEquals(new BigDecimal("2500.25"), savedEntity.getNetSalary());

        verify(eventPublisher).publish(eq(EventType.SALARY_SUMMARY_UPDATED), eq("u-1"), eq(request));
        assertEquals(new BigDecimal("3200.50"), response.userGrossSalary());
        assertEquals(new BigDecimal("2500.25"), response.userNetSalary());
        assertEquals(new BigDecimal("5000.00"), response.familyGrossSalary());
        assertEquals(new BigDecimal("4000.00"), response.familyNetSalary());
    }

    private Jwt buildJwt(String username) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("preferred_username", username)
                .build();
    }
}
