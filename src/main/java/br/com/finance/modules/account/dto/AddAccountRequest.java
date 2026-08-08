package br.com.finance.modules.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddAccountRequest(

        @NotBlank(message = "name é obrigatório")
        @Size(max = 50, message = "name deve ter no máximo 50 caracteres")
        String name,

        @NotNull(message = "bank é obrigatório")
        Integer bank,

        @NotNull(message = "type é obrigatório")
        Integer type,

        @NotNull(message = "link é obrigatório")
        Integer link

) {
}
