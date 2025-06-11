package br.com.abeatrizdev.account_service.service;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.entity.Account;
import br.com.abeatrizdev.account_service.entity.AccountStatus;
import br.com.abeatrizdev.account_service.mapper.AccountMapper;
import br.com.abeatrizdev.account_service.repository.AccountRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    private CreateAccountRequest createAccountRequest;
    private Account accountEntity;
    private Account accountSavedEntity;
    private AccountResponse accountResponse;

    @BeforeEach
    void setUp() {
        createAccountRequest = new CreateAccountRequest(
                "João Silva Santos",
                "12345678901"
        );

        accountEntity = new Account(
                "João Silva Santos",
                "12345678901",
                BigDecimal.ZERO,
                AccountStatus.ACTIVE
        );

        accountSavedEntity = new Account(
                1L,
                UUID.randomUUID(),
                "João Silva Santos",
                "12345678901",
                BigDecimal.ZERO,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        accountResponse = new AccountResponse(
                accountSavedEntity.getPublicId(),
                accountSavedEntity.getName(),
                accountSavedEntity.getDocument(),
                accountSavedEntity.getBalance(),
                accountSavedEntity.getStatus(),
                accountSavedEntity.getCreatedAt(),
                accountSavedEntity.getUpdatedAt()
        );
    }

    @AfterEach
    void tearDown() {
        reset(accountRepository, accountMapper);
    }

    @Nested
    @DisplayName("Account Create")
    class Create {

        @Test
        @DisplayName("Success")
        void givenValidAccountRequest_whenCreateAccount_thenReturnAccountResponse() {
            // Given
            given(accountMapper.toEntity(createAccountRequest)).willReturn(accountEntity);
            given(accountRepository.save(accountEntity)).willReturn(accountSavedEntity);
            given(accountMapper.toDTO(accountSavedEntity)).willReturn(accountResponse);

            // When
            var result = accountService.create(createAccountRequest);

            // Then
            assertAll("Account creation should return complete and valid response",
                    () -> assertNotNull(result),
                    () -> assertSame(accountResponse, result),
                    () -> assertEquals(accountResponse.publicId(), result.publicId()),
                    () -> assertEquals(accountResponse.name(), result.name()),
                    () -> assertEquals(accountResponse.document(), result.document()),
                    () -> assertEquals(accountResponse.balance(), result.balance()),
                    () -> assertEquals(accountResponse.status(), result.status()),
                    () -> assertEquals(accountResponse.createdAt(), result.createdAt()),
                    () -> assertEquals(accountResponse.updatedAt(), result.updatedAt())
            );

            assertAll("All service dependencies should be invoked correctly",
                    () -> verify(accountMapper, times(1)).toEntity(any(CreateAccountRequest.class)),
                    () -> verify(accountMapper, times(1)).toDTO(any(Account.class)),
                    () -> verify(accountRepository, times(1)).save(accountEntity)
            );
        }

        @Test
        @DisplayName("Failure - Null Request")
        void givenNullRequest_whenCreateAccount_thenThrowIllegalArgumentException() {
            // Given | When
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> accountService.create(null)
            );

            // Then
            assertAll("Exception should be thrown with details",
                    () -> assertNotNull(exception),
                    () -> assertTrue(exception.getMessage().contains("Request cannot be null")),
                    () -> assertInstanceOf(IllegalArgumentException.class, exception)
            );

            assertAll("No service dependencies should be invoked when request is null",
                    () -> verifyNoInteractions(accountMapper),
                    () -> verifyNoInteractions(accountRepository)
            );
        }

        @Test
        @DisplayName("Failure - Repository Save Error")
        void givenRepositorySaveFailure_whenCreateAccount_thenThrowRuntimeException() {
            // Given
            given(accountMapper.toEntity(createAccountRequest)).willReturn(accountEntity);
            given(accountRepository.save(accountEntity)).willThrow(new RuntimeException("Database connection failed"));

            // When
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> accountService.create(createAccountRequest)
            );

            // Then
            assertAll("Exception should be thrown with database error details",
                    () -> assertNotNull(exception),
                    () -> assertEquals("Database connection failed", exception.getMessage()),
                    () -> assertInstanceOf(RuntimeException.class, exception)
            );

            assertAll("Service should stop execution after repository failure",
                    () -> verify(accountMapper, times(1)).toEntity(eq(createAccountRequest)),
                    () -> verify(accountRepository, times(1)).save(eq(accountEntity)),
                    () -> verify(accountMapper, never()).toDTO((Account) any()),
                    () -> verifyNoMoreInteractions(accountMapper, accountRepository)
            );
        }

    }

    @Nested
    @DisplayName("Account FindById")
    class FindById {

    }

    @Nested
    @DisplayName("Account FindAll")
    class FindAll {

    }

    @Nested
    @DisplayName("Account Update")
    class Update {

    }

    @Nested
    @DisplayName("Account Delete")
    class Delete {

    }
}