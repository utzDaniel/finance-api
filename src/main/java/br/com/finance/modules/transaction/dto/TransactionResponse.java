package br.com.finance.modules.transaction.dto;

import br.com.finance.modules.account.dto.AccountUserResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        AccountUserResponse account,
        TransactionMethodResponse method,
        String name,
        boolean debit,
        LocalDate dateTransaction,
        BigDecimal amount
) {
    public TransactionResponse {
        amount = scaleToMoney(amount);
    }

    private static BigDecimal scaleToMoney(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }
}
