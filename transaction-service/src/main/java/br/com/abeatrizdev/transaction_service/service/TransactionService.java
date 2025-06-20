package br.com.abeatrizdev.transaction_service.service;

import br.com.abeatrizdev.transaction_service.dto.CreateTransactionRequest;
import br.com.abeatrizdev.transaction_service.dto.TransactionResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {


    @Transactional
    public TransactionResponse create(CreateTransactionRequest request) {
        // validar se fromAccountId e toAccountId sào iguais e caso seja retorne um erro
        return "";
    }

    /*
    @Transactional
    public List<TransactionResponse> findAll() {
        return List.of(new TransactionResponse());
    }

    @Transactional
    public TransactionResponse findByPublicId(UUID publicId) {
        return new TransactionResponse();
    }

    @Transactional
    public String findAllByAccountPublicId(UUID accountPublicId) {
        return "";
    }

    @Transactional
    public String softDelete(UUID publicId) {
        return "";
    }
     */
}
