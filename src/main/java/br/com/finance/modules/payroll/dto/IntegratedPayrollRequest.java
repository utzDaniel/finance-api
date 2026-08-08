package br.com.finance.modules.payroll.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record IntegratedPayrollRequest(

        @NotNull(message = "ids é obrigatório")
        @NotEmpty(message = "ids não pode ser vazio")
        List<Long> ids
) {
}
