package br.com.abeatrizdev.transaction_service.controller;

import br.com.abeatrizdev.transaction_service.dto.CreateTransactionRequest;
import br.com.abeatrizdev.transaction_service.dto.TransactionResponse;
import br.com.abeatrizdev.transaction_service.service.TransactionService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping
    public ResponseEntity<TransactionResponse> create(@RequestBody @Valid CreateTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(request));
    }


    /*
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.findAll());
    }


    @GetMapping("/{publicId}")
    public ResponseEntity<TransactionResponse> findByPublicId(
            @PathVariable UUID publicId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.findByPublicId(publicId));
    }


    @GetMapping("/account/{accountPublicId}")
    public ResponseEntity<?> findAllByAccountPublicId(
            @PathVariable UUID accountPublicId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.findAllByAccountPublicId(accountPublicId));
    }


    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> softDelete(
            @PathVariable UUID publicId
    ) {
        return ResponseEntity.ok(transactionService.softDelete(publicId));
    }
    */

}
