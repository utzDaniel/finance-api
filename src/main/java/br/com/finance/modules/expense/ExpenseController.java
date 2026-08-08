package br.com.finance.modules.expense;

import br.com.finance.config.TimestampUtils;
import br.com.finance.modules.expense.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/v1/finance")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/expense/{competence}")
    public ResponseEntity<Page<ExpenseResponse>> getExpense(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            @Valid @ModelAttribute ExpenseFilter filter
    ) {
        return ResponseEntity.ok(expenseService.getExpense(jwt, TimestampUtils.parseCompetence(competence), pageable, filter));
    }

    @PostMapping("/expense/{competence}")
    public ResponseEntity<Page<ExpenseResponse>> addExpense(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @RequestBody @Valid AddExpenseRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(expenseService.addExpense(jwt, TimestampUtils.parseCompetence(competence), request, pageable));
    }

    @PutMapping("/expense/{competence}")
    public ResponseEntity<Page<ExpenseResponse>> updateExpense(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @RequestBody @Valid UpdateExpenseRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(expenseService.updateExpense(jwt, TimestampUtils.parseCompetence(competence), request, pageable));
    }

    @DeleteMapping("/expense/{competence}")
    public ResponseEntity<Page<ExpenseResponse>> deleteExpense(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @RequestBody @Valid DeleteExpenseRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(expenseService.deleteExpense(jwt, TimestampUtils.parseCompetence(competence), request, pageable));
    }

    @PostMapping("/expense/{competence}/integrated")
    public ResponseEntity<Page<ExpenseResponse>> integratedPayroll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @RequestBody @Valid IntegratedExpenseRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(expenseService.integratedExpense(jwt, TimestampUtils.parseCompetence(competence), request, pageable));
    }

    @GetMapping("/expense/category")
    public ResponseEntity<List<ExpenseCategoryResponse>> getExpenseCategory() {
        return ResponseEntity.ok(expenseService.getExpenseCategory());
    }

    @GetMapping("/expense/shared")
    public ResponseEntity<List<ExpenseSharedResponse>> getExpenseShared() {
        return ResponseEntity.ok(expenseService.getExpenseShared());
    }

}
