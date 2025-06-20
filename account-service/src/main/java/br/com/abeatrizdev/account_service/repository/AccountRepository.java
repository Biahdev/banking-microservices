package br.com.abeatrizdev.account_service.repository;

import br.com.abeatrizdev.account_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByPublicId(UUID publicId);

}
