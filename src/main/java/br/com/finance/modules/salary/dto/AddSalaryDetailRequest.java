package br.com.finance.modules.salary.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AddSalaryDetailRequest(

        @NotNull(message = "idType é obrigatório")
        Integer idType,

        @NotNull(message = "code é obrigatório")
        @Min(value = 1, message = "code deve ser maior que zero")
        Integer code,

        @NotBlank(message = "name é obrigatório")
        @Size(max = 50, message = "name deve ter no máximo 50 caracteres")
        String name,

        @NotNull(message = "quantity é obrigatório")
        @Min(value = 1, message = "quantity não pode ser menor que zero")
        Integer quantity,

        @NotNull(message = "amount é obrigatório")
        @DecimalMin(value = "0.01", message = "amount não pode ser menor que zero")
        BigDecimal amount
) {
}
