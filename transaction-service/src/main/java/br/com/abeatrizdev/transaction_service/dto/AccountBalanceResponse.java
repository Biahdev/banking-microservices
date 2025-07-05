package br.com.abeatrizdev.transaction_service.dto;

import br.com.abeatrizdev.transaction_service.entity.AccountStatus;

import java.math.BigDecimal;

public record AccountBalanceResponse(
        AccountStatus status,
        BigDecimal balance
) {
}
