package br.com.abeatrizdev.transaction_service.service;

import br.com.abeatrizdev.transaction_service.dto.CreateTransactionRequest;
import br.com.abeatrizdev.transaction_service.dto.TransactionResponse;
import br.com.abeatrizdev.transaction_service.entity.AccountStatus;
import br.com.abeatrizdev.transaction_service.entity.TransactionStatus;
import br.com.abeatrizdev.transaction_service.mapper.TransactionMapper;
import br.com.abeatrizdev.transaction_service.repository.TransactionRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionMapper transactionMapper;

    private final TransactionRepository transactionRepository;

    private final AccountServiceClient accountServiceClient;

    public TransactionService(TransactionMapper transactionMapper, TransactionRepository transactionRepository, AccountServiceClient accountServiceClient) {
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
        this.accountServiceClient = accountServiceClient;
    }

    @Transactional
    public TransactionResponse create(CreateTransactionRequest request) {
        var transactionEntity = transactionMapper.toEntity(request);
        try {

            if (transactionEntity.getFromAccountId().equals(transactionEntity.getToAccountId())) {
                System.out.println("Source and destination accounts must be different");
                throw new IllegalArgumentException("Source and destination accounts must be different");
            }

            var fromAccountBalance = accountServiceClient.getById(transactionEntity.getFromAccountId());
            var toAccountBalance = accountServiceClient.getById(transactionEntity.getToAccountId());
            if (fromAccountBalance.balance() == null || toAccountBalance.balance() == null) {
                System.out.println("Contas não foram encontradas");
                transactionEntity.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transactionEntity);
                throw new RuntimeException("Contas não foram encontradas");
            }

            if (fromAccountBalance.status().equals(AccountStatus.INACTIVE) || toAccountBalance.status().equals(AccountStatus.INACTIVE)) {
                System.out.println("Uma das contas esta desativadas");
                transactionEntity.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transactionEntity);
                throw new RuntimeException("Uma das contas esta desativadas");
            }

            if (fromAccountBalance.balance().compareTo(transactionEntity.getAmount()) < 0) {
                System.out.println("Saldo insuficiente para transação");
                transactionEntity.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transactionEntity);
                throw new RuntimeException("Saldo insuficiente para transação");
            }

            System.out.println("====================================");
            System.out.println(fromAccountBalance);
            System.out.println(toAccountBalance);
            System.out.println("====================================");

            // KAFKA
            transactionEntity.setStatus(TransactionStatus.COMPLETED);
            transactionEntity = transactionRepository.save(transactionEntity);
            return transactionMapper.toDTO(transactionEntity);

        } catch (FeignException ex) {
            transactionEntity.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transactionEntity);
            System.out.println(ex.getMessage());
            System.out.println(ex.getCause());
            System.out.println(ex.getStackTrace());
            throw new RuntimeException("Transaction failed", ex.getCause());
        }

        // Validar se tem saldo na conta origem 2 contas
        // mensagem kafka
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
