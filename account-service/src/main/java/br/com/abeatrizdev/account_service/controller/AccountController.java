package br.com.abeatrizdev.account_service.controller;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.dto.account.UpdateAccountRequest;
import br.com.abeatrizdev.account_service.exception.ErrorMessage;
import br.com.abeatrizdev.account_service.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/accounts")
@Tag(name = "Contas Bancárias", description = "API para gerenciamento de contas bancárias")
public class AccountController {

    @Autowired
    private AccountService accountService;


    @Operation(
            summary = "Criar nova conta bancária",
            description = "Cria uma nova conta bancária com os dados fornecidos. O saldo inicial será zero e o status será ATIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Conta criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conta já existe com este documento",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<AccountResponse> create(
            @Parameter(
                    description = "Dados da conta a ser criada",
                    required = true
            )
            @RequestBody @Valid CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(request));
    }

    @Operation(
            summary = "Listar todas as contas",
            description = "Retorna uma lista com todas as contas bancárias cadastradas no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de contas retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.findAll());
    }

    @Operation(
            summary = "Buscar conta por ID",
            description = "Retorna os dados de uma conta específica através do seu identificador público único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conta encontrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de ID público inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conta não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
    @GetMapping("/{publicId}")
    public ResponseEntity<AccountResponse> findByPublicId(
            @Parameter(
                    description = "Identificador público único da conta",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID publicId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.findByPublicId(publicId));
    }

    @Operation(
            summary = "Atualizar dados da conta",
            description = "Atualiza os dados de uma conta existente. Apenas os campos fornecidos serão atualizados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conta atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conta não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito de dados com conta existente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
    @PutMapping("/{publicId}")
    public ResponseEntity<AccountResponse> update(
            @Parameter(
                    description = "Identificador público único da conta",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID publicId,
            @Parameter(
                    description = "Dados a serem atualizados na conta",
                    required = true
            )
            @RequestBody @Valid UpdateAccountRequest request
    ) {
        return ResponseEntity.ok(accountService.update(publicId, request));
    }

    @Operation(
            summary = "Desativar conta",
            description = "Realiza a desativação da conta, alterando seu status para INATIVO e Apenas contas com saldo zero podem ser desativadas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conta desativada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de ID público inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conta não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conta já está inativa ou possui saldo diferente de zero",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
    @DeleteMapping("/{publicId}")
    public ResponseEntity<AccountResponse> softDelete(
            @Parameter(
                    description = "Identificador público único da conta a ser desativada",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID publicId
    ) {
        return ResponseEntity.ok(accountService.softDelete(publicId));
    }

    @Operation(
            summary = "Reativar conta",
            description = "Reativa uma conta previamente desativada, alterando seu status para ATIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conta reativada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de ID público inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conta não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conta já está ativa",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
    @PutMapping("/{publicId}/reactivate")
    public ResponseEntity<AccountResponse> reactivate(
            @Parameter(
                    description = "Identificador público único da conta a ser reativada",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID publicId
    ) {
        return ResponseEntity.ok(accountService.reactivate(publicId));
    }
}
