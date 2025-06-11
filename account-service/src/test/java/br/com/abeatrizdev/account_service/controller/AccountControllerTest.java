package br.com.abeatrizdev.account_service.controller;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.entity.AccountStatus;
import br.com.abeatrizdev.account_service.service.AccountService;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Random.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest extends BaseControllerTest {

    @MockitoBean
    private AccountService accountService;

    private CreateAccountRequest createAccountRequest;

    private AccountResponse accountResponse;

    @BeforeEach
    void setUp() {
        baseUrl = "/accounts";
        resourceName = "accounts";

        createAccountRequest = new CreateAccountRequest(
                "João Silva",
                "12345678901"
        );

        accountResponse = new AccountResponse(
                UUID.randomUUID(),
                "João Silva",
                "12345678901",
                new BigDecimal("0"),
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Create Account - POST /accounts")
    class CreateAccount {

        @Test
        @DisplayName("Create Account Success")
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
        @DisplayName("Create Account Failure")
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


}
