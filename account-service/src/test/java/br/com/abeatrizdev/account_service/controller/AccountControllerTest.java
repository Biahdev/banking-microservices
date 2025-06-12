package br.com.abeatrizdev.account_service.controller;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.dto.account.UpdateAccountRequest;
import br.com.abeatrizdev.account_service.entity.AccountStatus;
import br.com.abeatrizdev.account_service.service.AccountService;
import br.com.abeatrizdev.account_service.utils.BaseControllerTest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Random.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest extends BaseControllerTest {

    @MockitoBean
    private AccountService accountService;

    private CreateAccountRequest createAccountRequest;

    private AccountResponse accountResponse;

    private AccountResponse accountResponse2;

    private AccountResponse deletedAccountResponse;

    private List<AccountResponse> listAccountResponse;

    private UpdateAccountRequest updateAccountRequest;

    private final UUID validPublicId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private final UUID nonExistentPublicId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

    @BeforeEach
    void setUp() {
        baseUrl = "/accounts";

        createAccountRequest = new CreateAccountRequest(
                "João Silva",
                "12345678901"
        );

        accountResponse = new AccountResponse(
                validPublicId,
                "João Silva",
                "12345678901",
                new BigDecimal("0"),
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        accountResponse2 = new AccountResponse(
                UUID.randomUUID(),
                "Maria Santos",
                "98765432100",
                new BigDecimal("2750.5"),
                AccountStatus.ACTIVE,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusHours(2)
        );

        updateAccountRequest = new UpdateAccountRequest(
                accountResponse.name(),
                accountResponse.document(),
                accountResponse.balance()
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

    @Nested
    @DisplayName("Account Create - POST /accounts")
    class Create {

        @Test
        @DisplayName("Success")
        void givenValidRequest_whenCreateAccount_thenReturnCreated() throws Exception {
            // Given
            given(accountService.create(any(CreateAccountRequest.class))).willReturn(accountResponse);

            // When
            var response = mockMvc.perform(post(baseUrl).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createAccountRequest)));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$.publicId").isNotEmpty(),
                            jsonPath("$.name").value(accountResponse.name()),
                            jsonPath("$.document").value(accountResponse.document()),
                            jsonPath("$.balance").value(accountResponse.balance()),
                            jsonPath("$.status").value(accountResponse.status().toString()),
                            jsonPath("$.createdAt").exists(),
                            jsonPath("$.updatedAt").exists()
                    );
        }

        @Test
        @DisplayName("Failure - Invalid request")
        void givenInvalidRequest_whenCreateAccount_thenReturnBadRequest() throws Exception {
            // Given
            var invalidCreateAccountRequest = new CreateAccountRequest(
                    "A",
                    ""
            );

            // When
            var response = mockMvc.perform(post(baseUrl).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCreateAccountRequest)));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("Request contains validation errors"),
                            jsonPath("$.details.name").exists(),
                            jsonPath("$.details.document").exists(),
                            jsonPath("$.details.name[0]").value("size must be between 3 and 100"),
                            jsonPath("$.details.document[0]").value("size must be between 8 and 14"),
                            jsonPath("$.details.document[1]").value("must not be blank")
                    );
        }
    }

    @Nested
    @DisplayName("Account FindAll - GET /accounts")
    class FindAll {

        @Test
        @DisplayName("Success - Multiple accounts list")
        void givenAccountsExist_whenFindAll_thenReturnAccountsList() throws Exception {
            // Given
            given(accountService.findAll()).willReturn(listAccountResponse);

            // When
            var response = mockMvc.perform(get(baseUrl).contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$", hasSize(2)),

                            jsonPath("$[0].publicId").value(accountResponse.publicId().toString()),
                            jsonPath("$[0].name").value(accountResponse.name()),
                            jsonPath("$[0].document").value(accountResponse.document()),
                            jsonPath("$[0].balance").value(accountResponse.balance()),
                            jsonPath("$[0].status").value(accountResponse.status().toString()),
                            jsonPath("$[0].createdAt").exists(),
                            jsonPath("$[0].updatedAt").exists(),

                            jsonPath("$[1].publicId").value(accountResponse2.publicId().toString()),
                            jsonPath("$[1].name").value(accountResponse2.name()),
                            jsonPath("$[1].document").value(accountResponse2.document()),
                            jsonPath("$[1].balance").value(accountResponse2.balance()),
                            jsonPath("$[1].status").value(accountResponse2.status().toString()),
                            jsonPath("$[1].createdAt").exists(),
                            jsonPath("$[1].updatedAt").exists()
                    );
        }

        @Test
        @DisplayName("Success - Empty list")
        void givenNoAccountsExist_whenFindAll_thenReturnEmptyList() throws Exception {
            // Given
            given(accountService.findAll()).willReturn(new ArrayList<>());

            // When
            var response = mockMvc.perform(get(baseUrl).contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$", hasSize(0)),
                            jsonPath("$").isArray()
                    );
        }
    }

    @Nested
    @DisplayName("Account FindById - GET /accounts/{publicId}")
    class FindById {

        @Test
        @DisplayName("Success")
        void givenValidPublicId_whenFindByPublicId_thenReturnAccountResponse() throws Exception {
            // Given
            given(accountService.findByPublicId(validPublicId)).willReturn(accountResponse);

            // When
            var response = mockMvc.perform(get(baseUrl + "/{publicId}", validPublicId).contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.publicId").value(accountResponse.publicId().toString()),
                            jsonPath("$.name").value(accountResponse.name()),
                            jsonPath("$.document").value(accountResponse.document()),
                            jsonPath("$.balance").value(accountResponse.balance()),
                            jsonPath("$.status").value(accountResponse.status().toString()),
                            jsonPath("$.createdAt").exists(),
                            jsonPath("$.updatedAt").exists()
                    );
        }

        @Test
        @DisplayName("Failure - Not existent publicId")
        void givenNonExistentPublicId_whenFindByPublicId_thenReturnNotFound() throws Exception {
            // Given
            given(accountService.findByPublicId(nonExistentPublicId))
                    .willThrow(new EntityNotFoundException("" + nonExistentPublicId));

            // When
            var response = mockMvc.perform(get(baseUrl + "/{publicId}", nonExistentPublicId).contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").value(404),
                            jsonPath("$.message").value("Resource not found: " + nonExistentPublicId),
                            jsonPath("$.timestamp").exists()
                    );
        }

        @DisplayName("Failure - Invalid publicId")
        @ParameterizedTest(name = "{0}")
        @MethodSource("br.com.abeatrizdev.account_service.utils.UtilsTest#invalidUuidProvider")
        void givenInvalidUuidFormat_whenFindByPublicId_thenReturnBadRequest(String invalidUuid) throws Exception {
            // Given & When
            var response = mockMvc.perform(get(baseUrl + "/{publicId}", invalidUuid)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.status").value(400),
                            jsonPath("$.message").exists(),
                            jsonPath("$.timestamp").exists()
                    );

        }
    }

    @Nested
    @DisplayName("Account Update - PUT /accounts/{publicId}")
    class Update {

        @Test
        @DisplayName("Success")
        void givenValidRequestAndExistingAccount_whenUpdate_thenReturnUpdatedAccount() throws Exception {
            // Given
            given(accountService.update(validPublicId, updateAccountRequest)).willReturn(accountResponse2);

            // When
            var response = mockMvc.perform(put(baseUrl + "/{publicId}", validPublicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateAccountRequest)));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.publicId").value(accountResponse2.publicId().toString()),
                            jsonPath("$.name").value(accountResponse2.name()),
                            jsonPath("$.document").value(accountResponse2.document()),
                            jsonPath("$.balance").value(accountResponse2.balance()),
                            jsonPath("$.status").value(accountResponse2.status().toString()),
                            jsonPath("$.createdAt").exists(),
                            jsonPath("$.updatedAt").exists()
                    );
        }

        @Test
        @DisplayName("Failure - Invalid request")
        void givenInvalidRequestData_whenUpdate_thenReturnBadRequest() throws Exception {
            // Given
            var invalidUpdateRequest = new UpdateAccountRequest(
                    "J",
                    "123abc",
                    new BigDecimal("100.540")
            );

            // When
            var response = mockMvc.perform(put(baseUrl + "/{publicId}", validPublicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidUpdateRequest)));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.status").value(400),
                            jsonPath("$.message").value("Request contains validation errors"),
                            jsonPath("$.details").exists(),
                            jsonPath("$.details.name").exists(),
                            jsonPath("$.details.document").exists(),
                            jsonPath("$.details.balance").exists(),
                            jsonPath("$.details.name[0]").value("size must be between 3 and 100"),
                            jsonPath("$.details.document[0]").value("size must be between 8 and 14"),
                            jsonPath("$.details.balance[0]").value("numeric value out of bounds (<9 digits>.<2 digits> expected)"),
                            jsonPath("$.timestamp").exists()
                    );
        }

        @Test
        @DisplayName("Failure - Not existent publicId")
        void givenNonExistentPublicId_whenUpdate_thenReturnNotFound() throws Exception {
            // Given
            given(accountService.update(nonExistentPublicId, updateAccountRequest))
                    .willThrow(new EntityNotFoundException("" + nonExistentPublicId));

            // When
            var response = mockMvc.perform(put(baseUrl + "/{publicId}", nonExistentPublicId, updateAccountRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateAccountRequest)));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").value(404),
                            jsonPath("$.message").value("Resource not found: " + nonExistentPublicId),
                            jsonPath("$.timestamp").exists()
                    );
        }

        @DisplayName("Failure - Invalid publicId")
        @ParameterizedTest(name = "{0}")
        @MethodSource("br.com.abeatrizdev.account_service.utils.UtilsTest#invalidUuidProvider")
        void givenInvalidUuidFormat_whenUpdate_thenReturnBadRequest(String invalidUuid) throws Exception {
            // Given & When
            var response = mockMvc.perform(put(baseUrl + "/{publicId}", invalidUuid)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.status").value(400),
                            jsonPath("$.message").exists(),
                            jsonPath("$.timestamp").exists()
                    );

        }
    }

    @Nested
    @DisplayName("Account SoftDelete - DELETE /accounts/{publicId}")
    class SoftDelete {

        @Test
        @DisplayName("Success")
        void givenActiveAccountWithZeroBalance_whenSoftDelete_thenReturnInactiveAccount() throws Exception {
            // Given
            given(accountService.softDelete(validPublicId)).willReturn(deletedAccountResponse);

            // When
            var response = mockMvc.perform(delete(baseUrl + "/{publicId}", validPublicId)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.publicId").value(validPublicId.toString()),
                            jsonPath("$.name").value(deletedAccountResponse.name()),
                            jsonPath("$.document").value(deletedAccountResponse.document()),
                            jsonPath("$.balance").value(deletedAccountResponse.balance()),
                            jsonPath("$.status").value("INACTIVE"),
                            jsonPath("$.updatedAt").exists()
                    );
        }

        @Test
        @DisplayName("Failure - already inactive Account")
        void givenAlreadyInactiveAccount_whenSoftDelete_thenReturnBadRequest() throws Exception {
            // Given
            given(accountService.softDelete(validPublicId))
                    .willThrow(new IllegalStateException("Account is already inactive"));

            // When
            var response = mockMvc.perform(delete(baseUrl + "/{publicId}", validPublicId)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isConflict(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.status").value(409),
                            jsonPath("$.message").value("Account is already inactive"),
                            jsonPath("$.timestamp").exists()
                    );
        }

        @Test
        @DisplayName("Failure - has non-zero balance")
        void givenAccountWithNonZeroBalance_whenSoftDelete_thenReturnBadRequest() throws Exception {
            // Given
            given(accountService.softDelete(validPublicId)).willThrow(new IllegalStateException("Cannot delete account with non-zero balance."));

            // When
            var response = mockMvc.perform(delete(baseUrl + "/{publicId}", validPublicId)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isConflict(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.status").value(409),
                            jsonPath("$.message").value("Cannot delete account with non-zero balance."),
                            jsonPath("$.timestamp").exists()
                    );
        }

        @Test
        @DisplayName("Failure - Not existent publicId")
        void givenNonExistentAccountId_whenSoftDelete_thenReturnNotFound() throws Exception {
            // Given
            given(accountService.softDelete(nonExistentPublicId))
                    .willThrow(new EntityNotFoundException("" + nonExistentPublicId));

            // When
            var response = mockMvc.perform(delete(baseUrl + "/{publicId}", nonExistentPublicId)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isNotFound(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.status").value(404),
                            jsonPath("$.message").value("Resource not found: " + nonExistentPublicId),
                            jsonPath("$.timestamp").exists()
                    );

        }

        @DisplayName("Failure - Invalid publicId")
        @ParameterizedTest(name = "{0}")
        @MethodSource("br.com.abeatrizdev.account_service.utils.UtilsTest#invalidUuidProvider")
        void givenInvalidUuidFormat_whenSoftDelete_thenReturnBadRequest(String invalidUuid) throws Exception {
            // Given & When
            var response = mockMvc.perform(delete(baseUrl + "/{publicId}", invalidUuid)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.status").value(400),
                            jsonPath("$.message").exists(),
                            jsonPath("$.timestamp").exists()
                    );

        }

    }

    @Nested
    @DisplayName("Account Reactive - PUT /{publicId}/reactivate ")
    class Reactive {

        @Test
        @DisplayName("Success")
        void givenInactiveAccount_whenReactivate_thenReturnActiveAccount() throws Exception {
            // Given
            given(accountService.reactivate(validPublicId)).willReturn(accountResponse);

            // When
            var response = mockMvc.perform(put(baseUrl + "/{publicId}/reactivate", validPublicId)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.publicId").value(validPublicId.toString()),
                            jsonPath("$.name").value(accountResponse.name()),
                            jsonPath("$.document").value(accountResponse.document()),
                            jsonPath("$.balance").value(accountResponse.balance()),
                            jsonPath("$.status").value(accountResponse.status().toString()),
                            jsonPath("$.createdAt").exists(),
                            jsonPath("$.updatedAt").exists()
                    );

        }

        @Test
        @DisplayName("Failure - already active Account")
        void givenAlreadyActiveAccount_whenReactivate_thenReturnBadRequest() throws Exception {
            // Given
            given(accountService.reactivate(validPublicId))
                    .willThrow(new IllegalStateException("Account is already active"));

            // When
            var response = mockMvc.perform(put(baseUrl + "/{publicId}/reactivate", validPublicId)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response
                    .andDo(print())
                    .andExpectAll(
                            status().isConflict(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.status").value(409),
                            jsonPath("$.message").value("Account is already active"),
                            jsonPath("$.timestamp").exists()
                    );
        }

        @Test
        @DisplayName("Failure - Not existent publicId")
        void givenNonExistentAccountId_whenReactivate_thenReturnNotFound() throws Exception {
            // Given
            given(accountService.reactivate(nonExistentPublicId))
                    .willThrow(new EntityNotFoundException("Account not found"));

            // When
            var response = mockMvc.perform(put(baseUrl + "/{publicId}/reactivate", nonExistentPublicId)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response
                    .andDo(print())
                    .andExpectAll(
                            status().isNotFound(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.status").value(404),
                            jsonPath("$.message").exists(),
                            jsonPath("$.timestamp").exists()
                    );
        }

        @DisplayName("Failure - Invalid publicId")
        @ParameterizedTest(name = "{0}")
        @MethodSource("br.com.abeatrizdev.account_service.utils.UtilsTest#invalidUuidProvider")
        void givenInvalidUuidFormat_whenReactivate_thenReturnBadRequest(String invalidUuid) throws Exception {
            // Given & When
            var response = mockMvc.perform(put(baseUrl + "/{publicId}/reactivate", invalidUuid)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.status").value(400),
                            jsonPath("$.message").exists(),
                            jsonPath("$.timestamp").exists()
                    );

        }
    }


}
