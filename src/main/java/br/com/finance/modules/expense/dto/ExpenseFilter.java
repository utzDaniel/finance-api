package br.com.finance.modules.expense.dto;

import br.com.finance.config.TimestampUtils;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record ExpenseFilter(
        @Pattern(
                regexp = TimestampUtils.DATA_REGEX,
                message = "due deve estar no formato yyyy-MM-dd"
        )
        String due,
        Long categoryId,
        String name
) {
    public String normalizedName() {
        if (name == null) return null;
        String value = name.trim();
        return value.isEmpty() ? null : value;
    }

    public LocalDate normalizedDate() {
        if (due == null) return null;
        return TimestampUtils.parse(due);
    }
}