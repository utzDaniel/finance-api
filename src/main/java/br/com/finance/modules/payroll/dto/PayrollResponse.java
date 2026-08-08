package br.com.finance.modules.payroll.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record PayrollResponse(
        Long id,
        EntryTypeResponse type,
        LocalDate entry,
        PayrollEventResponse event,
        int quantity,
        BigDecimal amount,
        boolean integrated
) {
    public PayrollResponse {
        amount = scaleToMoney(amount);
    }

    private static BigDecimal scaleToMoney(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }
}
