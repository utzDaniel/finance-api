package br.com.finance.modules.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferTransactionRequest(

        @NotNull(message = "accountOrigin é obrigatório")
        Integer accountOrigin,

        Integer accountDestination,

        @NotNull(message = "method é obrigatório")
        Integer method,

        @NotBlank(message = "name é obrigatório")
        @Size(max = 50, message = "name deve ter no máximo 50 caracteres")
        String name,

        @NotNull(message = "amount é obrigatório")
        @DecimalMin(value = "0.01", message = "amount não pode ser menor que zero")
        BigDecimal amount,

        @NotNull(message = "dateTransaction é obrigatório")
        LocalDate dateTransaction,

        @NotNull(message = "debit é obrigatório")
        Boolean debit

) {
}
