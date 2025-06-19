package br.com.abeatrizdev.account_service.service;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.dto.account.UpdateAccountRequest;
import br.com.abeatrizdev.account_service.entity.Account;
import br.com.abeatrizdev.account_service.entity.AccountStatus;
import br.com.abeatrizdev.account_service.mapper.AccountMapper;
import br.com.abeatrizdev.account_service.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.Random.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    private Account accountEntity;

    private Account inactiveAccount;

    private Account activeAccountWithZeroBalance;

    private CreateAccountRequest createAccountRequest;

    private AccountResponse accountResponse;

    private AccountResponse accountResponse2;

    private AccountResponse activeAccountResponse;

    private AccountResponse deletedAccountResponse;

    private List<AccountResponse> listAccountResponse;

    private UpdateAccountRequest updateAccountRequest;

    private final UUID validPublicId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private final UUID nonExistentPublicId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

    @BeforeEach
    void setUp() {
        accountEntity = new Account(
                1L,
                validPublicId,
                "João Silva",
                "12345678901",
                new BigDecimal("150"),
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        createAccountRequest = new CreateAccountRequest(
                "João Silva",
                "12345678901"
        );

        accountResponse = new AccountResponse(
                validPublicId,
                "João Silva",
                "12345678901",
                new BigDecimal("1500.50"),
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        accountResponse2 = new AccountResponse(
                nonExistentPublicId,
                "Maria Santos",
                "98765432100",
                new BigDecimal("0"),
                AccountStatus.ACTIVE,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusHours(2)
        );

        updateAccountRequest = new UpdateAccountRequest(
                accountResponse.name(),
                accountResponse.document(),
                accountResponse.balance()
        );

        activeAccountWithZeroBalance = new Account(
                1L,
                validPublicId,
                "João Silva",
                "12345678901",
                BigDecimal.ZERO,
                AccountStatus.ACTIVE,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        );

        inactiveAccount = new Account(
                1L,
                validPublicId,
                "João Silva",
                "12345678901",
                BigDecimal.ZERO,
                AccountStatus.INACTIVE,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        );

        activeAccountResponse = new AccountResponse(
                validPublicId,
                "João Silva",
                "12345678901",
                BigDecimal.ZERO,
                AccountStatus.ACTIVE,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        );

        deletedAccountResponse = new AccountResponse(
                validPublicId,
                "João Silva",
                "12345678901",
                BigDecimal.ZERO,
                AccountStatus.INACTIVE,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        );

        listAccountResponse = Arrays.asList(accountResponse, accountResponse2);
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
            given(accountRepository.save(accountEntity)).willReturn(accountEntity);
            given(accountMapper.toDTO(accountEntity)).willReturn(accountResponse);

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
        @DisplayName("Failure - Repository Exception")
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

        @Test
        @DisplayName("Failure - Duplicate document")
        void givenDuplicateDocument_whenCreate_thenThrowDataIntegrityViolationException() {
            // Given
            given(accountMapper.toEntity(createAccountRequest)).willReturn(accountEntity);
            given(accountRepository.save(accountEntity))
                    .willThrow(new DataIntegrityViolationException("Duplicate entry for document"));

            // When
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> accountService.create(createAccountRequest)
            );

            // Then
            assertAll("Data integrity exception should be thrown",
                    () -> assertNotNull(exception),
                    () -> assertTrue(exception.getMessage().contains("Duplicate entry")),
                    () -> assertInstanceOf(DataIntegrityViolationException.class, exception)
            );
            verify(accountMapper, times(1)).toEntity(createAccountRequest);
            verify(accountRepository, times(1)).save(accountEntity);
            verify(accountMapper, never()).toDTO(any(Account.class));
        }
    }

    @Nested
    @DisplayName("Account FindById")
    class FindById {

        @Test
        @DisplayName("Success")
        void givenExistingPublicId_whenFindByPublicId_thenReturnAccountResponse() {
            // Given
            given(accountRepository.findByPublicId(validPublicId))
                    .willReturn(Optional.of(accountEntity));
            given(accountMapper.toDTO(accountEntity))
                    .willReturn(accountResponse);

            // When
            AccountResponse result = accountService.findByPublicId(validPublicId);

            // Then
            assertAll("Account should be found and returned correctly",
                    () -> assertNotNull(result),
                    () -> assertSame(accountResponse, result),
                    () -> assertEquals(validPublicId, result.publicId()),
                    () -> assertEquals("João Silva", result.name()),
                    () -> assertEquals("12345678901", result.document()),
                    () -> assertEquals(new BigDecimal("1500.50"), result.balance()),
                    () -> assertEquals(AccountStatus.ACTIVE, result.status()),
                    () -> assertNotNull(result.createdAt()),
                    () -> assertNotNull(result.updatedAt())
            );

            assertAll("Dependencies should be called correctly",
                    () -> verify(accountRepository, times(1)).findByPublicId(validPublicId),
                    () -> verify(accountMapper, times(1)).toDTO(accountEntity)
            );

            verifyNoMoreInteractions(accountRepository, accountMapper);
        }

        @Test
        @DisplayName("Failure - Not existent publicId")
        void givenNonExistentPublicId_whenFindByPublicId_thenThrowEntityNotFoundException() {
            // Given
            given(accountRepository.findByPublicId(nonExistentPublicId))
                    .willReturn(Optional.empty());

            // When
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> accountService.findByPublicId(nonExistentPublicId));

            // Then
            assertAll("EntityNotFoundException should be thrown",
                    () -> assertNotNull(exception),
                    () -> assertInstanceOf(EntityNotFoundException.class, exception)
            );

            assertAll("Only repository should be called when account not found",
                    () -> verify(accountRepository, times(1)).findByPublicId(nonExistentPublicId),
                    () -> verifyNoInteractions(accountMapper)
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("br.com.abeatrizdev.account_service.utils.UtilsTest#invalidUuidProvider")
        @DisplayName("Failure - Invalid publicId")
        void givenInvalidUUID_whenFindByPublicId_thenThrowIllegalArgumentException(String invalidUuidString) {
            // Given e When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                UUID invalidUuid = UUID.fromString(invalidUuidString);
                accountService.findByPublicId(invalidUuid);
            });

            // Then
            assertAll("IllegalArgumentException should be thrown for invalid UUID",
                    () -> assertNotNull(exception),
                    () -> assertInstanceOf(IllegalArgumentException.class, exception)
            );

            verifyNoInteractions(accountRepository, accountMapper);
        }

    }

    @Nested
    @DisplayName("Account FindAll")
    class FindAll {
        @Test
        @DisplayName("Success - Multiple accounts")
        void givenMultipleAccountsExist_whenFindAll_thenReturnAccountsList() {
            // Given
            List<Account> accountEntities = Arrays.asList(
                    new Account(
                            1L,
                            validPublicId,
                            "João Silva",
                            "12345678901",
                            new BigDecimal("100.00"),
                            AccountStatus.ACTIVE,
                            LocalDateTime.now().minusDays(30),
                            LocalDateTime.now()
                    ),
                    new Account(
                            2L,
                            nonExistentPublicId,
                            "Maria Santos",
                            "98765432100",
                            new BigDecimal("2500.75"),
                            AccountStatus.ACTIVE,
                            LocalDateTime.now().minusDays(15),
                            LocalDateTime.now().minusHours(2)
                    )
            );

            given(accountRepository.findAll()).willReturn(accountEntities);
            given(accountMapper.toDTO(accountEntities)).willReturn(listAccountResponse);

            // When
            List<AccountResponse> result = accountService.findAll();

            // Then
            assertAll("Should return complete list of accounts",
                    () -> assertNotNull(result),
                    () -> assertSame(listAccountResponse, result),
                    () -> assertEquals(2, result.size()),
                    () -> assertEquals(accountResponse.publicId(), result.getFirst().publicId()),
                    () -> assertEquals(accountResponse.name(), result.getFirst().name()),
                    () -> assertEquals(accountResponse2.publicId(), result.get(1).publicId()),
                    () -> assertEquals(accountResponse2.name(), result.get(1).name())
            );

            assertAll("All service dependencies should be invoked correctly",
                    () -> verify(accountRepository, times(1)).findAll(),
                    () -> verify(accountMapper, times(1)).toDTO(accountEntities)
            );

            verifyNoMoreInteractions(accountRepository, accountMapper);
        }

        @Test
        @DisplayName("Success - Empty")
        void givenNoAccountsExist_whenFindAll_thenReturnEmptyList() {
            // Given
            List<Account> emptyAccountEntities = List.of();
            List<AccountResponse> emptyAccountResponses = List.of();

            given(accountRepository.findAll()).willReturn(emptyAccountEntities);
            given(accountMapper.toDTO(emptyAccountEntities)).willReturn(emptyAccountResponses);

            // When
            List<AccountResponse> result = accountService.findAll();

            // Then
            assertAll("Should return empty list when no accounts exist",
                    () -> assertNotNull(result),
                    () -> assertSame(emptyAccountResponses, result),
                    () -> assertTrue(result.isEmpty()),
                    () -> assertEquals(0, result.size())
            );

            assertAll("All service dependencies should be invoked correctly",
                    () -> verify(accountRepository, times(1)).findAll(),
                    () -> verify(accountMapper, times(1)).toDTO(emptyAccountEntities)
            );

            verifyNoMoreInteractions(accountRepository, accountMapper);
        }

        @Test
        @DisplayName("Failure - Repository Exception")
        void givenRepositoryThrowsException_whenFindAll_thenPropagateException() {
            // Given
            given(accountRepository.findAll())
                    .willThrow(new RuntimeException("Database connection timeout"));

            // When
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> accountService.findAll()
            );

            // Then
            assertAll("Exception should be propagated from repository layer",
                    () -> assertNotNull(exception),
                    () -> assertEquals("Database connection timeout", exception.getMessage()),
                    () -> assertInstanceOf(RuntimeException.class, exception)
            );

            assertAll("Service should fail immediately on repository error",
                    () -> verify(accountRepository, times(1)).findAll(),
                    () -> verify(accountMapper, never()).toDTO(any(List.class)),
                    () -> verifyNoMoreInteractions(accountRepository, accountMapper)
            );
        }

    }

    @Nested
    @DisplayName("Account Update")
    class Update {

        @Test
        @DisplayName("Success")
        void givenValidPublicIdAndRequest_whenUpdateAccount_thenReturnUpdatedAccountResponse() {
            // Given
            given(accountRepository.findByPublicId(validPublicId)).willReturn(Optional.of(accountEntity));
            given(accountMapper.toDTO(accountEntity)).willReturn(accountResponse);

            // When
            var result = accountService.update(validPublicId, updateAccountRequest);

            // Then
            assertAll("Account update should return complete and valid updated response",
                    () -> assertNotNull(result),
                    () -> assertSame(accountResponse, result),
                    () -> assertEquals(validPublicId, result.publicId()),
                    () -> assertEquals(accountResponse.name(), result.name()),
                    () -> assertEquals(accountResponse.document(), result.document()),
                    () -> assertEquals(accountResponse.balance(), result.balance()),
                    () -> assertEquals(accountResponse.status(), result.status())
            );

            assertAll("All service dependencies should be invoked correctly",
                    () -> verify(accountRepository, times(1)).findByPublicId(validPublicId),
                    () -> verify(accountMapper, times(1)).toDTO(accountEntity)
            );
        }

        @Test
        @DisplayName("Failure - Not existent publicId")
        void givenNonExistentPublicId_whenUpdateAccount_thenThrowEntityNotFoundException() {
            // Given
            given(accountRepository.findByPublicId(nonExistentPublicId)).willReturn(Optional.empty());

            // When
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> accountService.update(nonExistentPublicId, updateAccountRequest)
            );

            // Then
            assertAll("Exception should be thrown with proper details",
                    () -> assertNotNull(exception),
                    () -> assertInstanceOf(EntityNotFoundException.class, exception)
            );

            assertAll("Service should stop execution after entity not found",
                    () -> verify(accountRepository, times(1)).findByPublicId(nonExistentPublicId),
                    () -> verify(accountMapper, never()).toDTO(any(Account.class)),
                    () -> verifyNoMoreInteractions(accountRepository, accountMapper)
            );
        }

    }

    @Nested
    @DisplayName("Account SoftDelete")
    class SoftDelete {
        @Test
        @DisplayName("Success")
        void givenActiveAccountWithZeroBalance_whenSoftDelete_thenReturnInactiveAccount() {
            // Given
            given(accountRepository.findByPublicId(validPublicId))
                    .willReturn(Optional.of(activeAccountWithZeroBalance));
            given(accountMapper.toDTO(activeAccountWithZeroBalance))
                    .willReturn(deletedAccountResponse);

            // When
            AccountResponse result = accountService.softDelete(validPublicId);

            // Then
            assertAll("Should successfully soft delete account and return inactive account",
                    () -> assertNotNull(result),
                    () -> assertSame(deletedAccountResponse, result),
                    () -> assertEquals(validPublicId, result.publicId()),
                    () -> assertEquals("João Silva", result.name()),
                    () -> assertEquals("12345678901", result.document()),
                    () -> assertEquals(BigDecimal.ZERO, result.balance()),
                    () -> assertEquals(AccountStatus.INACTIVE, result.status())
            );

            assertAll("All service dependencies should be invoked correctly",
                    () -> verify(accountRepository, times(1)).findByPublicId(validPublicId),
                    () -> verify(accountMapper, times(1)).toDTO(activeAccountWithZeroBalance)
            );
            assertEquals(AccountStatus.INACTIVE, activeAccountWithZeroBalance.getStatus());

            verifyNoMoreInteractions(accountRepository, accountMapper);
        }

        @Test
        @DisplayName("Failure - Not existent publicId")
        void givenNonExistentPublicId_whenSoftDelete_thenThrowEntityNotFoundException() {
            // Given
            given(accountRepository.findByPublicId(nonExistentPublicId))
                    .willReturn(Optional.empty());

            // When
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> accountService.softDelete(nonExistentPublicId)
            );

            // Then
            assertAll("Exception should be thrown when account not found",
                    () -> assertNotNull(exception),
                    () -> assertInstanceOf(EntityNotFoundException.class, exception)
            );

            assertAll("Service should stop execution after entity not found",
                    () -> verify(accountRepository, times(1)).findByPublicId(nonExistentPublicId),
                    () -> verify(accountMapper, never()).toDTO(any(Account.class)),
                    () -> verifyNoMoreInteractions(accountRepository, accountMapper)
            );
        }

        @Test
        @DisplayName("Failure - Already inactive")
        void givenInactiveAccount_whenSoftDelete_thenThrowIllegalStateException() {
            // Given
            given(accountRepository.findByPublicId(validPublicId))
                    .willReturn(Optional.of(inactiveAccount));

            // When
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> accountService.softDelete(validPublicId)
            );

            // Then
            assertAll("Exception should be thrown when account is already inactive",
                    () -> assertNotNull(exception),
                    () -> assertEquals("Account is already inactive", exception.getMessage()),
                    () -> assertInstanceOf(IllegalStateException.class, exception)
            );

            assertAll("Service should find account but fail on business rule validation",
                    () -> verify(accountRepository, times(1)).findByPublicId(validPublicId),
                    () -> verify(accountMapper, never()).toDTO(any(Account.class)),
                    () -> verifyNoMoreInteractions(accountRepository, accountMapper)
            );

            // Verificar que o status permanece INACTIVE
            assertEquals(AccountStatus.INACTIVE, inactiveAccount.getStatus());
        }

        @Test
        @DisplayName("Failure - Has non-zero balance")
        void givenActiveAccountWithNonZeroBalance_whenSoftDelete_thenThrowIllegalStateException() {
            // Given
            given(accountRepository.findByPublicId(validPublicId))
                    .willReturn(Optional.of(accountEntity));

            // When
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> accountService.softDelete(validPublicId)
            );

            // Then
            assertAll("Exception should be thrown when account has non-zero balance",
                    () -> assertNotNull(exception),
                    () -> assertEquals("Cannot delete account with non-zero balance.", exception.getMessage()),
                    () -> assertInstanceOf(IllegalStateException.class, exception)
            );

            assertAll("Service should find account but fail on business rule validation",
                    () -> verify(accountRepository, times(1)).findByPublicId(validPublicId),
                    () -> verify(accountMapper, never()).toDTO(any(Account.class)),
                    () -> verifyNoMoreInteractions(accountRepository, accountMapper)
            );

            assertEquals(AccountStatus.ACTIVE, accountEntity.getStatus());
            assertEquals(new BigDecimal("150"), accountEntity.getBalance());
        }
    }

    @Nested
    @DisplayName("Account Reactive")
    class Reactive {

        @Test
        @DisplayName("Success")
        void givenInactiveAccount_whenReactivate_thenReturnActiveAccount() {
            // Given
            given(accountRepository.findByPublicId(validPublicId)).willReturn(Optional.of(inactiveAccount));
            given(accountMapper.toDTO(inactiveAccount)).willReturn(activeAccountResponse);

            // When
            AccountResponse result = accountService.reactivate(validPublicId);

            // Then
            assertAll("Should successfully reactivate account and return active account",
                    () -> assertNotNull(result),
                    () -> assertSame(activeAccountResponse, result),
                    () -> assertEquals(validPublicId, result.publicId()),
                    () -> assertEquals("João Silva", result.name()),
                    () -> assertEquals("12345678901", result.document()),
                    () -> assertEquals(new BigDecimal("0"), result.balance()),
                    () -> assertEquals(AccountStatus.ACTIVE, result.status())
            );

            assertAll("All service dependencies should be invoked correctly",
                    () -> verify(accountRepository, times(1)).findByPublicId(validPublicId),
                    () -> verify(accountMapper, times(1)).toDTO(inactiveAccount)
            );

            verifyNoMoreInteractions(accountRepository, accountMapper);
        }

        @Test
        @DisplayName("Failure - Not existent publicId")
        void givenNonExistentPublicId_whenReactivate_thenThrowEntityNotFoundException() {
            // Given
            given(accountRepository.findByPublicId(nonExistentPublicId)).willReturn(Optional.empty());

            // When
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> accountService.reactivate(nonExistentPublicId)
            );

            // Then
            assertAll("Exception should be thrown when account not found",
                    () -> assertNotNull(exception),
                    () -> assertInstanceOf(EntityNotFoundException.class, exception)
            );

            assertAll("Service should stop execution after entity not found",
                    () -> verify(accountRepository, times(1)).findByPublicId(nonExistentPublicId),
                    () -> verify(accountMapper, never()).toDTO(any(Account.class)),
                    () -> verifyNoMoreInteractions(accountRepository, accountMapper)
            );
        }

        @Test
        @DisplayName("Failure - Already active")
        void givenActiveAccount_whenReactivate_thenThrowIllegalStateException() {
            // Given
            given(accountRepository.findByPublicId(validPublicId)).willReturn(Optional.of(activeAccountWithZeroBalance));

            // When
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> accountService.reactivate(validPublicId)
            );

            // Then
            assertEquals(AccountStatus.ACTIVE, activeAccountWithZeroBalance.getStatus());
            assertAll("Exception should be thrown when account is already active",
                    () -> assertNotNull(exception),
                    () -> assertEquals("Account is already active", exception.getMessage()),
                    () -> assertInstanceOf(IllegalStateException.class, exception)
            );

            assertAll("Service should find account but fail on business rule validation",
                    () -> verify(accountRepository, times(1)).findByPublicId(validPublicId),
                    () -> verify(accountMapper, never()).toDTO(any(Account.class)),
                    () -> verifyNoMoreInteractions(accountRepository, accountMapper)
            );
        }

    }


}