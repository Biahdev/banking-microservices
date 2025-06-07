package br.com.abeatrizdev.account_service.repository;

import br.com.abeatrizdev.account_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
