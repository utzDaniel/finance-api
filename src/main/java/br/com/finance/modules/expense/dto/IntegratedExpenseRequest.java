package br.com.finance.modules.expense.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record IntegratedExpenseRequest(

        @NotNull(message = "ids é obrigatório")
        @NotEmpty(message = "ids não pode ser vazio")
        List<Long> ids,

        @NotNull(message = "account é obrigatório")
        Long account,

        @NotNull(message = "method é obrigatório")
        Integer method,

        LocalDate dateTransaction,

        @NotNull(message = "payDue é obrigatório")
        Boolean payDue

) {
}
