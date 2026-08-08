package br.com.finance.modules.account.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record AccountResponse(
        Integer id,
        String name,
        AccountBankResponse bank,
        AccountTypeResponse type,
        AccountLinkResponse link,
        BigDecimal balance
) {
    public AccountResponse {
        balance = scaleToMoney(balance);
    }

    private static BigDecimal scaleToMoney(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }
}