package br.com.finance.modules.summary.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateMonthlySummaryRequest(

        @NotNull(message = "UserGrossSalary é obrigatório")
        @DecimalMin(value = "0.00", message = "UserGrossSalary deve ser maior ou igual a zero")
        BigDecimal userGrossSalary,

        @NotNull(message = "UserNetSalary é obrigatório")
        @DecimalMin(value = "0.00", message = "UserNetSalary deve ser maior ou igual a zero")
        BigDecimal userNetSalary

) {
}