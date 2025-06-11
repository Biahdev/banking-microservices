package br.com.abeatrizdev.account_service.service;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.mapper.AccountMapper;
import br.com.abeatrizdev.account_service.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        var accountEntity = accountMapper.toEntity(request);
        accountEntity = accountRepository.save(accountEntity);
        return accountMapper.toDTO(accountEntity);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        var accountEntity = accountRepository.findAll();
        return accountMapper.toDTO(accountEntity);
    }

    @Transactional(readOnly = true)
    public AccountResponse findByPublicId(UUID publicId) {
        var accountEntity = accountRepository.findByPublicId(publicId).orElseThrow(EntityNotFoundException::new);
        return accountMapper.toDTO(accountEntity);
    }

    @Transactional(readOnly = true)
    public AccountResponse findByInternalId(Long id) {
        var accountEntity = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return accountMapper.toDTO(accountEntity);
    }


}
