package br.com.abeatrizdev.account_service.controller;

import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody @Valid CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.findAll());
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<AccountResponse> findByPublicId(@PathVariable UUID publicId) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.findByPublicId(publicId));
    }






}
