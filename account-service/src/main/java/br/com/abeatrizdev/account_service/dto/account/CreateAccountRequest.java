package br.com.abeatrizdev.account_service.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank
        @Size(min = 3, max = 150)
        String name,

        @NotBlank
        @Size(min = 3, max = 12)
        String document
) {
}
