package br.com.abeatrizdev.account_service.service;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.dto.account.UpdateAccountRequest;
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

    private final AccountMapper accountMapper;

    private final AccountRepository accountRepository;

    public AccountService(AccountMapper accountMapper, AccountRepository accountRepository) {
        this.accountMapper = accountMapper;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
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

    @Transactional()
    public AccountResponse update(UUID publicId, UpdateAccountRequest request) {
        var accountEntity = accountRepository.findByPublicId(publicId).orElseThrow(EntityNotFoundException::new);
        accountEntity.update(request);
        return accountMapper.toDTO(accountEntity);
    }

    @Transactional()
    public AccountResponse softDelete(UUID publicId) {
        var accountEntity = accountRepository.findByPublicId(publicId).orElseThrow(EntityNotFoundException::new);
        accountEntity.softDelete();
        return accountMapper.toDTO(accountEntity);
    }

    @Transactional()
    public AccountResponse reactivate(UUID publicId) {
        var accountEntity = accountRepository.findByPublicId(publicId).orElseThrow(EntityNotFoundException::new);
        accountEntity.activate();
        return accountMapper.toDTO(accountEntity);
    }


}
