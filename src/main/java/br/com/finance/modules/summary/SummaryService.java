package br.com.finance.modules.summary;

import br.com.finance.config.ApiException;
import br.com.finance.config.Violacao;
import br.com.finance.modules.event.EventPublisher;
import br.com.finance.modules.event.EventType;
import br.com.finance.modules.keycloak.KeycloakService;
import br.com.finance.modules.keycloak.dto.KeycloakUserFamilyRecord;
import br.com.finance.modules.summary.dto.MonthlySummaryResponse;
import br.com.finance.modules.summary.dto.SalaryEntity;
import br.com.finance.modules.summary.dto.SumSalaryDto;
import br.com.finance.modules.summary.dto.UpdateMonthlySummaryRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SummaryService {

    private final SalaryRepository salaryRepository;
    private final KeycloakService keycloakService;
    private final EventPublisher eventPublisher;

    public SummaryService(
            SalaryRepository salaryRepository,
            EventPublisher eventPublisher,
            KeycloakService keycloakService
    ) {
        this.salaryRepository = salaryRepository;
        this.keycloakService = keycloakService;
        this.eventPublisher = eventPublisher;
    }

    public MonthlySummaryResponse getMonthlySummary(Jwt jwt, LocalDate competenceDate) {
        KeycloakUserFamilyRecord userFamily = keycloakService.getUserFamily(jwt);

        SalaryEntity salaryEntity = salaryRepository
                .findByUserIdAndCompetenceDate(userFamily.userId(), competenceDate)
                .orElseGet(SalaryEntity::new);

        salaryEntity.setUserId(userFamily.userId());
        salaryEntity.setCompetenceDate(competenceDate);

        return getMonthlySummaryResponse(userFamily, salaryEntity);
    }

    private MonthlySummaryResponse getMonthlySummaryResponse(KeycloakUserFamilyRecord userFamily, SalaryEntity salaryEntity) {

        BigDecimal familyGrossSalary = BigDecimal.ZERO;
        BigDecimal familyNetSalary = BigDecimal.ZERO;

        if (userFamily.familyId() != null) {
            List<String> familyUserIds = keycloakService.getFamilyUserIds(userFamily.familyId());
            SumSalaryDto sumSalaryDto = salaryRepository
                    .sumSalaryByUserIdInAndCompetenceDate(familyUserIds, salaryEntity.getCompetenceDate());
            familyGrossSalary = sumSalaryDto.getGrossSalary();
            familyNetSalary = sumSalaryDto.getNetSalary();
        }

        return new MonthlySummaryResponse(
                userFamily.nome(),
                userFamily.sobrenome(),
                userFamily.familyName(),
                salaryEntity.getGrossSalary(),
                familyGrossSalary,
                salaryEntity.getNetSalary(),
                familyNetSalary
        );
    }


    @Transactional
    public MonthlySummaryResponse updateMonthlySummary(Jwt jwt, LocalDate competenceDate, UpdateMonthlySummaryRequest request) throws ApiException {

        if (request.userNetSalary().compareTo(request.userGrossSalary()) > 0) {
            throw ApiException.badRequest(List.of(
                    new Violacao("userNetSalary", "Salário líquido não pode ser maior que o salário bruto"),
                    new Violacao("userGrossSalary", "Salário bruto não pode ser menor que o salário líquido")
            ));
        }

        KeycloakUserFamilyRecord userFamily = keycloakService.getUserFamily(jwt);

        SalaryEntity salaryEntity = salaryRepository
                .findByUserIdAndCompetenceDate(userFamily.userId(), competenceDate)
                .orElseGet(SalaryEntity::new);

        if (salaryEntity.getId() == null) {
            salaryEntity.setUserId(userFamily.userId());
            salaryEntity.setCompetenceDate(competenceDate);
        }

        salaryEntity.setGrossSalary(request.userGrossSalary());
        salaryEntity.setNetSalary(request.userNetSalary());

        salaryRepository.save(salaryEntity);
        eventPublisher.publish(EventType.SALARY_SUMMARY_UPDATED, userFamily.userId(), request);

        return getMonthlySummaryResponse(userFamily, salaryEntity);
    }

}