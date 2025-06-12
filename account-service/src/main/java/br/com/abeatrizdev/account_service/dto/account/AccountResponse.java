package br.com.abeatrizdev.account_service.dto.account;

import br.com.abeatrizdev.account_service.entity.AccountStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Resposta contendo informações da conta bancária")
public record AccountResponse(

        @Schema(description = "Identificador público único da conta", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID publicId,

        @Schema(description = "Nome do titular da conta", example = "João Silva", maxLength = 100)
        String name,

        @Schema(description = "Documento do titular (CPF/CNPJ)", example = "123.456.789-00")
        String document,

        @Schema(description = "Saldo atual da conta", example = "1500.75", minimum = "0")
        BigDecimal balance,

        @Schema(description = "Status atual da conta", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        AccountStatus status,

        @Schema(description = "Data e hora de criação da conta", example = "15-03-2024 14:30", pattern = "dd-MM-yyyy hh:mm")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora da última atualização da conta", example = "20-05-2024 09:15", pattern = "dd-MM-yyyy hh:mm")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm")
        LocalDateTime updatedAt
) {
}