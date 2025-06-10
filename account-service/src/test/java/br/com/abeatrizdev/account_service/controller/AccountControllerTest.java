package br.com.abeatrizdev.account_service.controller;

import br.com.abeatrizdev.account_service.dto.account.AccountResponse;
import br.com.abeatrizdev.account_service.dto.account.CreateAccountRequest;
import br.com.abeatrizdev.account_service.entity.AccountStatus;
import br.com.abeatrizdev.account_service.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Random.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateAccountRequest createAccountRequest;
    private AccountResponse accountResponse;

    @BeforeEach
    void setUp() {
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
        void givenValidRequest_whenCreateAccount_thenReturnCreated() throws Exception {
            given(accountService.create(any(CreateAccountRequest.class))).willReturn(accountResponse);

            String urlPost = "/accounts";
            var response = mockMvc.perform(post(urlPost)
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
        void givenInvalidRequest_whenCreateAccount_thenReturnBadRequest() throws Exception {
            String urlPost = "/accounts";
            var invalidJson = """
                    {
                        "name": "1",
                        "document": ""
                    }""";
            var response = mockMvc.perform(post(urlPost)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson));

            response
                    .andDo(print())
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("Request contains validation errors"),
                            jsonPath("$.fields.name").exists(),
                            jsonPath("$.fields.document").exists()
                    );
        }
    }


}
