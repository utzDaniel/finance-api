package br.com.finance.modules.expense.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        LocalDate due,
        ExpenseSharedResponse shared,
        String name,
        BigDecimal amount,
        ExpenseCategoryResponse category,
        String detail,
        boolean integrated
) {
    public ExpenseResponse {
        amount = scaleToMoney(amount);
    }

    private static BigDecimal scaleToMoney(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }
}