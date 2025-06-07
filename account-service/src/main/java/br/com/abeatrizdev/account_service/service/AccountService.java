package br.com.abeatrizdev.account_service.service;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.mapper.AccountMapper;
import br.com.abeatrizdev.account_service.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        var clientEntity = accountMapper.toEntity(request);
        clientEntity = accountRepository.save(clientEntity);

        return accountMapper.toDTO(clientEntity);
    }


}
