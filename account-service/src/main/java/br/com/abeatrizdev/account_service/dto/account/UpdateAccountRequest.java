package br.com.abeatrizdev.account_service.dto.account;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateAccountRequest(

        @Size(min = 3, max = 100)
        String name,

        @Size(min = 8, max = 14)
        String document,

        @Digits(integer = 9, fraction = 2)
        BigDecimal balance
) {
}
