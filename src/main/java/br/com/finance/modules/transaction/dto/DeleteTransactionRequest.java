package br.com.finance.modules.transaction.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DeleteTransactionRequest(

        @NotNull(message = "ids é obrigatório")
        @NotEmpty(message = "ids não pode ser vazio")
        List<Long> ids
) {
}
