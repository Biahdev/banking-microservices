package br.com.abeatrizdev.account_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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

    protected String resourceName;

    @Nested
    @DisplayName("Common API Validation")
    class CommonControllerTests {

        @Test
        @DisplayName("Invalid resource")
        void givenInvalidResource_whenRequest_thenReturnNotFoundWithErrorMessage() throws Exception {
            //Given | When
            var response = mockMvc.perform(get("/recurso_invalido"));

            //Then
            response
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is("Recurso não encontrado")))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status", is(404)));

        }

        @Test
        @DisplayName("Invalid HTTP Method")
        void givenUnsupportedHttpMethod_whenRequest_thenReturnsMethodNotAllowed() throws Exception {
            // Given | When
            var response = mockMvc.perform(put(baseUrl));

            // Then
            response
                    .andDo(print())
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(jsonPath("$.status", is(405)))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        @DisplayName("Invalid JSON return ErrorMessage")
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
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.message", containsString("JSON")));
        }

    }


}
