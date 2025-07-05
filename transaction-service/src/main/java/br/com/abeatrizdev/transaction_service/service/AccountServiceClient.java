package br.com.abeatrizdev.transaction_service.service;

import br.com.abeatrizdev.transaction_service.dto.AccountBalanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "account-service", url = "http://localhost:8081")
public interface AccountServiceClient {

    @GetMapping("/accounts/{accountPublicId}")
    AccountBalanceResponse getById(@PathVariable("accountPublicId") UUID accountPublicId);

}
