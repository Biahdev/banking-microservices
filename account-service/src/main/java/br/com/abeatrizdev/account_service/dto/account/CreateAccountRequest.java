package br.com.abeatrizdev.account_service.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação de nova conta bancária")
public record CreateAccountRequest(

        @NotBlank
        @Size(min = 3, max = 100)
        @Schema(description = "Nome do titular da conta", example = "João Silva", maxLength = 100)
        String name,

        @NotBlank
        @Size(min = 8, max = 14)
        @Schema(description = "Documento do titular (CPF/CNPJ)", example = "123.456.789-00")
        String document

) {
}
