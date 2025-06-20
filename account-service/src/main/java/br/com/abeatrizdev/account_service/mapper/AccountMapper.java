package br.com.abeatrizdev.account_service.mapper;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "publicId", ignore = true)
    Account toEntity(CreateAccountRequest category);

    AccountResponse toDTO(Account clientEntity);

    List<AccountResponse> toDTO(List<Account> clientEntity);

}
