package br.com.abeatrizdev.account_service.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para atualização de conta bancária existente")
public record UpdateAccountRequest(

        @Size(min = 3, max = 100)
        @Schema(description = "Nome do titular da conta", example = "João Silva", maxLength = 100)
        String name,

        @Size(min = 8, max = 14)
        @Schema(description = "Documento do titular (CPF/CNPJ)", example = "123.456.789-00", nullable = true)
        String document,

        @Digits(integer = 9, fraction = 2)
        @Schema(description = "Saldo atual da conta", example = "1500.75", minimum = "0", nullable = true)
        BigDecimal balance
) {
}
