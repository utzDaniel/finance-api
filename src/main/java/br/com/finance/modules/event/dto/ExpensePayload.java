package br.com.finance.modules.event.dto;

import br.com.finance.modules.expense.dto.ExpenseEntity;

import java.time.LocalDate;
import java.util.List;

public record ExpensePayload(
        List<ExpenseEntity> entities,
        Long account,
        Integer method,
        LocalDate dateTransaction,
        Boolean payDue
) {
}
