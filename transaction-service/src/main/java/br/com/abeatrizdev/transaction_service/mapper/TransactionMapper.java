package br.com.abeatrizdev.transaction_service.mapper;

import br.com.abeatrizdev.transaction_service.dto.CreateTransactionRequest;
import br.com.abeatrizdev.transaction_service.dto.TransactionResponse;
import br.com.abeatrizdev.transaction_service.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "publicId", ignore = true)
    Transaction toEntity(CreateTransactionRequest request);

    TransactionResponse toDTO(Transaction clientEntity);
}
