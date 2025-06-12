package br.com.abeatrizdev.account_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String baseUrl;

    @Nested
    @DisplayName("Common API Validation")
    class CommonControllerTests {

        @Test
        @DisplayName("Invalid resource return error message")
        void givenInvalidResource_whenRequest_thenReturnNotFoundWithErrorMessage() throws Exception {
            //Given | When
            var response = mockMvc.perform(get("/recurso_invalido"));

            //Then
            response
                    .andDo(print())
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").value(404),
                            jsonPath("$.message").value("Resource not found"),
                            jsonPath("$.timestamp").exists()
                    );

        }

        @Test
        @DisplayName("Invalid HTTP Method return error message")
        void givenUnsupportedHttpMethod_whenRequest_thenReturnsMethodNotAllowed() throws Exception {
            // Given | When
            var response = mockMvc.perform(patch(baseUrl));

            // Then
            response
                    .andDo(print())
                    .andExpectAll(
                            status().isMethodNotAllowed(),
                            jsonPath("$.status").value(405),
                            jsonPath("$.message").value("HTTP method not supported"),
                            jsonPath("$.timestamp").exists()
                    );
        }

        @Test
        @DisplayName("Invalid JSON return error message")
        void givenInvalidJson_whenCreateCategory_thenReturnBadRequestWithErrorMessage() throws Exception {
            // Given
            var json = """
                    {
                        "invalid": ""
                        "json": ""
                    }""";

            // When
            var response = mockMvc.perform(post(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json));

            //Then
            response
                    .andDo(print())
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").value(400),
                            jsonPath("$.message").value("Invalid JSON"),
                            jsonPath("$.timestamp").exists()
                    );
        }

    }


}
