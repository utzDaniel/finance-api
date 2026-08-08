package br.com.finance.modules.summary.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record SummaryResponse(
        BigDecimal grossSalary,
        BigDecimal netSalary,
        BigDecimal expense,
        BigDecimal expensePay
) {
    public SummaryResponse {
        grossSalary = scaleToMoney(grossSalary);
        netSalary = scaleToMoney(netSalary);
        expense = scaleToMoney(expense);
        expensePay = scaleToMoney(expensePay);
    }

    private static BigDecimal scaleToMoney(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }
}