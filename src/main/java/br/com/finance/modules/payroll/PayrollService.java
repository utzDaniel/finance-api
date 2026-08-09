package br.com.finance.modules.payroll;

import br.com.finance.config.ApiException;
import br.com.finance.config.Violacao;
import br.com.finance.modules.competence.CompetenceService;
import br.com.finance.modules.competence.dto.CompetenceResponse;
import br.com.finance.modules.competence.dto.CompetenceStatus;
import br.com.finance.modules.event.EventProcessorEngine;
import br.com.finance.modules.event.dto.EventAction;
import br.com.finance.modules.event.dto.EventPayload;
import br.com.finance.modules.event.dto.EventType;
import br.com.finance.modules.event.dto.PayrollPayload;
import br.com.finance.modules.keycloak.KeycloakService;
import br.com.finance.modules.payroll.dto.*;
import br.com.finance.modules.summary.dto.SummaryPayrollDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final KeycloakService keycloakService;
    private final EventProcessorEngine eventProcessorEngine;
    private final CompetenceService competenceService;

    public PayrollService(
            PayrollRepository payrollRepository,
            KeycloakService keycloakService,
            EventProcessorEngine eventProcessorEngine,
            CompetenceService competenceService
    ) {
        this.payrollRepository = payrollRepository;
        this.keycloakService = keycloakService;
        this.eventProcessorEngine = eventProcessorEngine;
        this.competenceService = competenceService;
    }

    public Page<PayrollResponse> getPayroll(Jwt jwt, LocalDate competence, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);
        return payrollRepository
                .findAllByUserIdAndCompetence(userId, competence, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public Page<PayrollResponse> addPayroll(Jwt jwt, LocalDate competence, AddPayrollRequest request, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceResponse competenceResponse = competenceService.getCompetence(jwt, competence);
        if (competenceResponse.status().id() != CompetenceStatus.ABERTA.getId()) {
            throw ApiException.badRequest("Mês já está fechado");
        }

        findTypeOrThrow(request.type());
        findEventOrThrow(request.event());

        PayrollEntity entity = PayrollEntity.builder()
                .userId(userId)
                .competence(competence)
                .type(request.type())
                .event(request.event())
                .quantity(request.quantity())
                .amount(request.amount())
                .entry(request.entry())
                .build();

        payrollRepository.save(entity);
        eventProcessorEngine.process(new EventPayload<>(EventType.PAYROLL, EventAction.ADDED, userId, new PayrollPayload(List.of(entity))));

        return getPayroll(jwt, competence, pageable);
    }

    @Transactional
    public Page<PayrollResponse> updatePayroll(Jwt jwt, LocalDate competence, UpdatePayrollRequest request, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceResponse competenceResponse = competenceService.getCompetence(jwt, competence);
        if (competenceResponse.status().id() != CompetenceStatus.ABERTA.getId()) {
            throw ApiException.badRequest("Mês já está fechado");
        }

        PayrollEntity entity = payrollRepository.findByIdUserIdAndCompetence(request.id(), userId, competence)
                .orElseThrow(() -> ApiException.notFound("Lançamento não encontrado"));

        findTypeOrThrow(request.type());
        findEventOrThrow(request.event());

        if (payrollRepository.existsIntegratedById(request.id()) > 0) {
            throw ApiException.badRequest("Lançamento já integrado");
        }

        entity.setType(request.type());
        entity.setEvent(request.event());
        entity.setQuantity(request.quantity());
        entity.setAmount(request.amount());
        entity.setEntry(request.entry());

        payrollRepository.save(entity);
        eventProcessorEngine.process(new EventPayload<>(EventType.PAYROLL, EventAction.UPDATED, userId, new PayrollPayload(List.of(entity))));

        return getPayroll(jwt, competence, pageable);
    }

    @Transactional
    public Page<PayrollResponse> deletePayroll(Jwt jwt, LocalDate competence, DeletePayrollRequest request, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceResponse competenceResponse = competenceService.getCompetence(jwt, competence);
        if (competenceResponse.status().id() != CompetenceStatus.ABERTA.getId()) {
            throw ApiException.badRequest("Mês já está fechado");
        }

        List<PayrollEntity> entities = payrollRepository.findAllByUserIdAndCompetence(request.ids(), userId, competence);

        if (entities.isEmpty() || entities.size() != request.ids().size()) {
            throw ApiException.notFound("Lançamentos não encontrados ");
        }

        eventProcessorEngine.process(new EventPayload<>(EventType.PAYROLL, EventAction.DELETED, userId, new PayrollPayload(entities)));

        return getPayroll(jwt, competence, pageable);
    }

    @Transactional
    public Page<PayrollResponse> integratedPayroll(Jwt jwt, LocalDate competence, IntegratedPayrollRequest request, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceResponse competenceResponse = competenceService.getCompetence(jwt, competence);
        if (competenceResponse.status().id() != CompetenceStatus.ABERTA.getId()) {
            throw ApiException.badRequest("Mês já está fechado");
        }

        List<PayrollEntity> entities = payrollRepository.findAllByUserIdAndCompetence(request.ids(), userId, competence);

        if (entities.isEmpty() || entities.size() != request.ids().size()) {
            throw ApiException.notFound("Lançamentos não encontrados");
        }

        eventProcessorEngine.process(new EventPayload<>(EventType.PAYROLL, EventAction.INTEGRATED, userId, new PayrollPayload(entities)));

        return getPayroll(jwt, competence, pageable);
    }

    private void findTypeOrThrow(int type) {
        if (!EntryType.isValid(type)) {
            throw ApiException.badRequest(List.of(
                    new Violacao("type", "Tipo não encontrado: " + type)
            ));
        }
    }

    private void findEventOrThrow(int event) {
        if (!PayrollEvent.isValid(event)) {
            throw ApiException.badRequest(List.of(
                    new Violacao("event", "Evento não encontrado: " + event)
            ));
        }
    }

    private PayrollResponse toResponse(PayrollDto dto) {
        EntryType type = EntryType.get(dto.getType());
        assert type != null;
        PayrollEvent event = PayrollEvent.get(dto.getEvent());
        assert event != null;
        return new PayrollResponse(
                dto.getId(),
                new EntryTypeResponse(type.getId(), type.getDescription()),
                dto.getEntry(),
                new PayrollEventResponse(event.getId(), event.getDescription()),
                dto.getQuantity(),
                dto.getAmount(),
                dto.getIntegrated() > 0 || type.getId() == EntryType.DESCONTO.getId()
        );
    }

    public List<PayrollEventResponse> getPayrollEvent() {
        return Arrays.stream(PayrollEvent.values()).map(v -> new PayrollEventResponse(v.getId(), v.getDescription())).toList();
    }

    public List<EntryTypeResponse> getPayrollType() {
        return Arrays.stream(EntryType.values()).map(v -> new EntryTypeResponse(v.getId(), v.getDescription())).toList();
    }

    public SummaryPayrollDto getSummaryPayroll(String userId, LocalDate competence) {
        return payrollRepository.findSummaryPayroll(userId, competence);
    }
}
