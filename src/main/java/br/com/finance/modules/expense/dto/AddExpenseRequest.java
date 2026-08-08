package br.com.finance.modules.expense.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AddExpenseRequest(

        @NotNull(message = "due é obrigatório")
        LocalDate due,

        @NotNull(message = "shared é obrigatório")
        boolean shared,

        @NotBlank(message = "name é obrigatório")
        @Size(max = 50, message = "name deve ter no máximo 50 caracteres")
        String name,

        @NotNull(message = "amount é obrigatório")
        @DecimalMin(value = "0.01", message = "amount não pode ser menor que zero")
        BigDecimal amount,

        @NotNull(message = "category é obrigatório")
        Integer category,

        @Size(max = 250, message = "detail deve ter no máximo 250 caracteres")
        String detail

) {
}
