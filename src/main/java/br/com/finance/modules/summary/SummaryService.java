package br.com.finance.modules.summary;

import br.com.finance.modules.expense.ExpenseService;
import br.com.finance.modules.keycloak.KeycloakService;
import br.com.finance.modules.payroll.PayrollService;
import br.com.finance.modules.summary.dto.SummaryExpenseDto;
import br.com.finance.modules.summary.dto.SummaryPayrollDto;
import br.com.finance.modules.summary.dto.SummaryResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SummaryService {

    private final KeycloakService keycloakService;
    private final PayrollService payrollService;
    private final ExpenseService expenseService;

    public SummaryService(KeycloakService keycloakService, PayrollService payrollService, ExpenseService expenseService) {
        this.keycloakService = keycloakService;
        this.payrollService = payrollService;
        this.expenseService = expenseService;
    }

    public SummaryResponse getSummary(Jwt jwt, LocalDate competence) {
        String userId = keycloakService.getIdUser(jwt);

        SummaryPayrollDto summaryPayroll = payrollService.getSummaryPayroll(userId, competence);
        SummaryExpenseDto summaryExpense = expenseService.getSummaryExpense(userId, competence);

        return new SummaryResponse(summaryPayroll.getGrossSalary(), summaryPayroll.getNetSalary(),
                summaryExpense.getExpense(), summaryExpense.getExpensePay());
    }

}