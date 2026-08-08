package br.com.finance.modules.payroll.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AddPayrollRequest(

        @NotNull(message = "type é obrigatório")
        Integer type,

        @NotNull(message = "event é obrigatório")
        Integer event,

        @NotNull(message = "quantity é obrigatório")
        @Min(value = 1, message = "quantity não pode ser menor que zero")
        Integer quantity,

        @NotNull(message = "amount é obrigatório")
        @DecimalMin(value = "0.01", message = "amount não pode ser menor que zero")
        BigDecimal amount,

        @NotNull(message = "entry é obrigatório")
        LocalDate entry
) {
}
