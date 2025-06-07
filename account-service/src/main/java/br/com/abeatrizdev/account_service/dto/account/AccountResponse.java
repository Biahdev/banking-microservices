package br.com.abeatrizdev.account_service.dto.account;

import br.com.abeatrizdev.account_service.entity.AccountStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID publicId,

        String name,

        String document,

        BigDecimal balance,

        AccountStatus status,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm")
        LocalDateTime createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm")
        LocalDateTime updatedAt
) {
}
