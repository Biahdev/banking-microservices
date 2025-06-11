package br.com.abeatrizdev.account_service.controller;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private List<AccountResponse> listAccountResponse;

    private final UUID validPublicId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private final UUID invalidPublicId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

    @BeforeEach
    void setUp() {
        baseUrl = "/accounts";
        resourceName = "accounts";

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

        listAccountResponse = Arrays.asList(accountResponse, accountResponse2);

    }

    @Nested
    @DisplayName("Account Create - POST /accounts")
    class Create {

        @Test
        @DisplayName("Success")
        void givenValidRequest_whenCreateAccount_thenReturnCreated() throws Exception {
            given(accountService.create(any(CreateAccountRequest.class))).willReturn(accountResponse);

            var response = mockMvc.perform(post(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createAccountRequest)));

            response
                    .andDo(print())
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$.publicId").isNotEmpty(),
                            jsonPath("$.name").value(accountResponse.name()),
                            jsonPath("$.document").value(accountResponse.document()),
                            jsonPath("$.balance").value(0.0),
                            jsonPath("$.status").value("ACTIVE"),
                            jsonPath("$.createdAt").isNotEmpty(),
                            jsonPath("$.updatedAt").isNotEmpty()
                    );
        }

        @Test
        @DisplayName("Failure - Invalid request")
        void givenInvalidRequest_whenCreateAccount_thenReturnBadRequest() throws Exception {
            var invalidJson = """
                    {
                        "name": "1",
                        "document": ""
                    }""";
            var response = mockMvc.perform(post(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson));

            response
                    .andDo(print())
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("Request contains validation errors"),
                            jsonPath("$.details.name").exists(),
                            jsonPath("$.details.document").exists()
                    );
        }
    }

    @Nested
    @DisplayName("Accounts FindAll - GET /accounts")
    class FindAll {

        @Test
        @DisplayName("Success")
        void givenAccountsExist_whenFindAll_thenReturnAccountsList() throws Exception {
            // Given
            given(accountService.findAll()).willReturn(listAccountResponse);

            // When & Then
            mockMvc.perform(get(baseUrl).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
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

            // When & Then
            mockMvc.perform(get(baseUrl).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
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

            // When & Then
            mockMvc.perform(get(baseUrl + "/{publicId}", validPublicId).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.publicId").value(accountResponse.publicId().toString()),
                            jsonPath("$.name").value(accountResponse.name()),
                            jsonPath("$.document").value(accountResponse.document()),
                            jsonPath("$.balance").value(0.0),
                            jsonPath("$.status").value("ACTIVE"),
                            jsonPath("$.createdAt").isNotEmpty(),
                            jsonPath("$.updatedAt").isNotEmpty()
                    );
        }

        @Test
        @DisplayName("Failure - publicId Not Found")
        void givenInvalidPublicId_whenFindByPublicId_thenReturnNotFound() throws Exception {
            // Given
            given(accountService.findByPublicId(invalidPublicId))
                    .willThrow(new EntityNotFoundException("" + invalidPublicId));

            // When & Then
            mockMvc.perform(get(baseUrl + "/{publicId}", invalidPublicId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").value(404),
                            jsonPath("$.message").value("Entity not found with id " + invalidPublicId),
                            jsonPath("$.timestamp").exists()
                    );
        }

        @DisplayName("Failure - Invalid publicId")
        @ParameterizedTest(name = "{0}")
        @MethodSource("br.com.abeatrizdev.account_service.utils.UtilsTest#invalidUuidProvider")
        void givenInvalidUuidFormat_whenFindByPublicId_thenReturnBadRequest(String invalidUuid) throws Exception {
            // When
            var response = mockMvc.perform(get(baseUrl + "/{publicId}", invalidUuid)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            response
                    .andDo(print())
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
    @DisplayName("Account Update")
    class Update {

    }

    @Nested
    @DisplayName("Account Delete")
    class Delete {

    }


}
