package br.com.finance.modules.transaction.dto;

import br.com.finance.config.TimestampUtils;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record TransactionFilter(
        @Pattern(
                regexp = TimestampUtils.DATA_REGEX,
                message = "dateTransaction deve estar no formato yyyy-MM-dd"
        )
        String dateTransaction,
        Integer methodId,
        String name,
        String accountName
) {
    public String normalizedName() {
        if (name == null) return null;
        String value = name.trim();
        return value.isEmpty() ? null : value;
    }

    public String normalizedaccountName() {
        if (accountName == null) return null;
        String value = accountName.trim();
        return value.isEmpty() ? null : value;
    }

    public LocalDate normalizedDate() {
        if (dateTransaction == null) return null;
        return TimestampUtils.parse(dateTransaction);
    }
}