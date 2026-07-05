package br.com.finance.modules.summary.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record MonthlySummaryResponse(
        String nome,
        String sobrenome,
        String familyName,
        BigDecimal userGrossSalary,
        BigDecimal familyGrossSalary,
        BigDecimal userNetSalary,
        BigDecimal familyNetSalary
) {
    public MonthlySummaryResponse {
        userGrossSalary = scaleToMoney(userGrossSalary);
        familyGrossSalary = scaleToMoney(familyGrossSalary);
        userNetSalary = scaleToMoney(userNetSalary);
        familyNetSalary = scaleToMoney(familyNetSalary);
    }

    private static BigDecimal scaleToMoney(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }
}