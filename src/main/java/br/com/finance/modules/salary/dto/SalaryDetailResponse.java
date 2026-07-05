package br.com.finance.modules.salary.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record SalaryDetailResponse(
        int idType,
        int code,
        String name,
        int quantity,
        BigDecimal amount
) {
    public SalaryDetailResponse {
        amount = scaleToMoney(amount);
    }

    private static BigDecimal scaleToMoney(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }
}
