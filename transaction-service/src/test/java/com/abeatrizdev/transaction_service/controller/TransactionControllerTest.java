package com.abeatrizdev.transaction_service.controller;


import br.com.abeatrizdev.transaction_service.controller.TransactionController;
import br.com.abeatrizdev.transaction_service.dto.CreateTransactionRequest;
import br.com.abeatrizdev.transaction_service.dto.TransactionResponse;
import br.com.abeatrizdev.transaction_service.entity.TransactionStatus;
import br.com.abeatrizdev.transaction_service.entity.TransactionType;
import br.com.abeatrizdev.transaction_service.service.TransactionService;
import com.abeatrizdev.transaction_service.utils.BaseControllerTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
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

@WebMvcTest(TransactionController.class)
@TestMethodOrder(MethodOrderer.Random.class)
@ContextConfiguration(classes = {TransactionController.class})
public class TransactionControllerTest extends BaseControllerTest {

    @MockitoBean
    private TransactionService transactionService;

    private CreateTransactionRequest createTransactionRequest;

    private TransactionResponse transactionResponse;

    private final String description = "";

    private final UUID validPublicId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private final UUID nonExistentPublicId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

    private final UUID fromAccountId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    private final UUID toAccountId = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");


    @BeforeEach
    void setUp() {
        var description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        baseUrl = "/transactions";

        createTransactionRequest = new CreateTransactionRequest(
                fromAccountId,
                toAccountId,
                new BigDecimal("100"),
                TransactionType.PIX,
                description
        );

        transactionResponse = new TransactionResponse(
                validPublicId,
                fromAccountId,
                toAccountId,
                new BigDecimal("100"),
                description,
                TransactionType.PIX,
                TransactionStatus.COMPLETED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Transaction Create - POST /transactions")
    class Create {

        @Test
        @DisplayName("Success")
        void givenValidRequest_whenCreateAccount_thenReturnCreated() throws Exception {
            // Given
            given(transactionService.create(any(CreateTransactionRequest.class))).willReturn(transactionResponse);

            // When
            var response = mockMvc.perform(post(baseUrl).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTransactionRequest)));

            // Then
            response.andDo(print())
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$.publicId").isNotEmpty(),
                            jsonPath("$.fromAccountId").value(transactionResponse.fromAccountId()),
                            jsonPath("$.toAccountId").value(transactionResponse.toAccountId()),
                            jsonPath("$.amount").value(transactionResponse.amount()),
                            jsonPath("$.description").value(transactionResponse.description()),
                            jsonPath("$.type").value(transactionResponse.type().toString()),
                            jsonPath("$.status").value(transactionResponse.status().toString()),
                            jsonPath("$.createdAt").exists(),
                            jsonPath("$.updatedAt").exists()
                    );
        }

        @Test
        @DisplayName("Failure - fromAccountId e toAccountId iguais")
        void asdas() throws Exception {

        }


    }
}
