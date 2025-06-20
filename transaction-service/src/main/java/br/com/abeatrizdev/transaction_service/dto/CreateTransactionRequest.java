package br.com.abeatrizdev.transaction_service.dto;
import br.com.abeatrizdev.transaction_service.entity.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull
        UUID fromAccountId,

        @NotNull
        UUID toAccountId,

        @NotNull
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        @DecimalMax(value = "999999.99", message = "Amount cannot exceed 999,999.99")
        @Digits(integer = 6, fraction = 2, message = "Amount must have at most 6 integer digits and 2 decimal places")
        BigDecimal amount,

        @NotNull
        TransactionType type,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description
) {
}
