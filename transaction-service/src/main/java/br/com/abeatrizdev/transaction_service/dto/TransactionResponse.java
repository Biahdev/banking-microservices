package br.com.abeatrizdev.transaction_service.dto;

import br.com.abeatrizdev.transaction_service.entity.TransactionStatus;
import br.com.abeatrizdev.transaction_service.entity.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(

        UUID publicId,

        UUID fromAccountId,

        UUID toAccountId,

        BigDecimal amount,

        TransactionType type,

        String description,

        TransactionStatus status,

        String referenceNumber,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime updatedAt

) {
}
