package br.com.finance.modules.expense;

import br.com.finance.config.ApiException;
import br.com.finance.config.Violacao;
import br.com.finance.modules.competence.CompetenceService;
import br.com.finance.modules.competence.dto.CompetenceResponse;
import br.com.finance.modules.competence.dto.CompetenceStatus;
import br.com.finance.modules.event.EventProcessorEngine;
import br.com.finance.modules.event.dto.EventAction;
import br.com.finance.modules.event.dto.EventPayload;
import br.com.finance.modules.event.dto.EventType;
import br.com.finance.modules.event.dto.ExpensePayload;
import br.com.finance.modules.expense.dto.*;
import br.com.finance.modules.keycloak.KeycloakService;
import br.com.finance.modules.summary.dto.SummaryExpenseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final KeycloakService keycloakService;
    private final EventProcessorEngine eventProcessorEngine;
    private final CompetenceService competenceService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            KeycloakService keycloakService,
            EventProcessorEngine eventProcessorEngine,
            CompetenceService competenceService
    ) {
        this.expenseRepository = expenseRepository;
        this.keycloakService = keycloakService;
        this.eventProcessorEngine = eventProcessorEngine;
        this.competenceService = competenceService;
    }

    public Page<ExpenseResponse> getExpense(Jwt jwt, LocalDate competence, Pageable pageable, ExpenseFilter filter) {
        String userId = keycloakService.getIdUser(jwt);
        return expenseRepository
                .findAllByUserIdAndCompetence(
                        userId,
                        competence,
                        filter != null ? filter.normalizedDate() : null,
                        filter != null ? filter.categoryId() : null,
                        filter != null ? filter.normalizedName() : null,
                        pageable
                )
                .map(this::toResponse);
    }

    @Transactional
    public Page<ExpenseResponse> addExpense(Jwt jwt, LocalDate competence, AddExpenseRequest request, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceResponse competenceResponse = competenceService.getCompetence(jwt, competence);
        if (competenceResponse.status().id() != CompetenceStatus.ABERTA.getId()) {
            throw ApiException.badRequest("Mês já está fechado");
        }

        findCategoryOrThrow(request.category());

        ExpenseEntity expense = ExpenseEntity.builder()
                .userId(userId)
                .competence(competence)
                .due(request.due())
                .shared(request.shared())
                .name(request.name())
                .amount(request.amount())
                .category(request.category())
                .detail(request.detail())
                .build();

        expenseRepository.save(expense);
        eventProcessorEngine.process(new EventPayload<>(EventType.EXPENSE, EventAction.ADDED, userId,
                new ExpensePayload(List.of(expense), null, null, null, null)));

        return getExpense(jwt, competence, pageable, null);
    }

    @Transactional
    public Page<ExpenseResponse> updateExpense(Jwt jwt, LocalDate competence, UpdateExpenseRequest request, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceResponse competenceResponse = competenceService.getCompetence(jwt, competence);
        if (competenceResponse.status().id() != CompetenceStatus.ABERTA.getId()) {
            throw ApiException.badRequest("Mês já está fechado");
        }

        ExpenseEntity expense = expenseRepository.findByIdUserIdAndCompetence(request.id(), userId, competence)
                .orElseThrow(() -> ApiException.notFound("Despesa não encontrado"));

        findCategoryOrThrow(request.category());

        if (expenseRepository.existsIntegratedById(request.id()) > 0) {
            throw ApiException.badRequest("Despesa já integrada");
        }

        expense.setDue(request.due());
        expense.setShared(request.shared());
        expense.setName(request.name());
        expense.setAmount(request.amount());
        expense.setDetail(request.detail());
        expense.setCategory(request.category());

        expenseRepository.save(expense);
        eventProcessorEngine.process(new EventPayload<>(EventType.EXPENSE, EventAction.UPDATED, userId,
                new ExpensePayload(List.of(expense), null, null, null, null)));

        return getExpense(jwt, competence, pageable, null);
    }

    @Transactional
    public Page<ExpenseResponse> deleteExpense(Jwt jwt, LocalDate competence, DeleteExpenseRequest request, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceResponse competenceResponse = competenceService.getCompetence(jwt, competence);
        if (competenceResponse.status().id() != CompetenceStatus.ABERTA.getId()) {
            throw ApiException.badRequest("Mês já está fechado");
        }

        List<ExpenseEntity> expenses = expenseRepository.findAllByDetailIdInAndUserId(request.ids(), userId, competence);

        if (expenses.isEmpty() || expenses.size() != request.ids().size()) {
            throw ApiException.notFound("Despesas não encontrados ");
        }

        eventProcessorEngine.process(new EventPayload<>(EventType.EXPENSE, EventAction.DELETED, userId,
                new ExpensePayload(expenses, null, null, null, null)));

        return getExpense(jwt, competence, pageable, null);
    }

    @Transactional
    public Page<ExpenseResponse> integratedExpense(Jwt jwt, LocalDate competence, IntegratedExpenseRequest request, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceResponse competenceResponse = competenceService.getCompetence(jwt, competence);
        if (competenceResponse.status().id() != CompetenceStatus.ABERTA.getId()) {
            throw ApiException.badRequest("Mês já está fechado");
        }

        if (!request.payDue() && request.dateTransaction() == null) {
            throw ApiException.badRequest(List.of(
                    new Violacao("dateTransaction", "Data do pagamento é obrigatório")
            ));
        }

        List<ExpenseEntity> expenses = expenseRepository.findAllByDetailIdInAndUserId(request.ids(), userId, competence);

        if (expenses.isEmpty() || expenses.size() != request.ids().size()) {
            throw ApiException.notFound("Despesas não encontrados ");
        }

        eventProcessorEngine.process(new EventPayload<>(EventType.EXPENSE, EventAction.INTEGRATED, userId,
                new ExpensePayload(expenses, request.account(), request.method(), request.dateTransaction(), request.payDue())));

        return getExpense(jwt, competence, pageable, null);
    }

    private void findCategoryOrThrow(int category) {
        if (!ExpenseCategory.isValid(category)) {
            throw ApiException.badRequest(List.of(
                    new Violacao("category", "Categoria não encontrado: " + category)
            ));
        }
    }

    private ExpenseResponse toResponse(ExpensePaymentDto dto) {
        ExpenseCategory category = ExpenseCategory.get(dto.getCategory());
        assert category != null;
        ExpenseShared shared = ExpenseShared.get(dto.getShared());
        assert shared != null;
        return new ExpenseResponse(
                dto.getId(),
                dto.getDue(),
                new ExpenseSharedResponse(shared.getValue(), shared.getDescription()),
                dto.getName(),
                dto.getAmount(),
                new ExpenseCategoryResponse(category.getId(), category.getDescription()),
                dto.getDetail(),
                dto.getIntegrated() > 0
        );
    }

    public SummaryExpenseDto getSummaryExpense(String userId, LocalDate competence) {
        return expenseRepository.findSummaryExpense(userId, competence);
    }

    public List<ExpenseCategoryResponse> getExpenseCategory() {
        return Arrays.stream(ExpenseCategory.values()).map(v -> new ExpenseCategoryResponse(v.getId(), v.getDescription())).toList();
    }

    public List<ExpenseSharedResponse> getExpenseShared() {
        return Arrays.stream(ExpenseShared.values()).map(v -> new ExpenseSharedResponse(v.getValue(), v.getDescription())).toList();
    }

}
