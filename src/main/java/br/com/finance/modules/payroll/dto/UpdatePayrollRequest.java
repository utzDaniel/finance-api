package br.com.finance.modules.payroll.dto;

import br.com.finance.config.TimestampUtils;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdatePayrollRequest(

        @NotNull(message = "id é obrigatório")
        Long id,

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
