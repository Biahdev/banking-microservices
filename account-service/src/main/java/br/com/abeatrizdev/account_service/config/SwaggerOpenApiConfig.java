package br.com.abeatrizdev.account_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition
@Configuration
public class SwaggerOpenApiConfig {

    @Bean
    public OpenAPI userOpenAPI() {
        Info info = new Info()
                .title("Banking Account Service API")
                .version("1.0")
                .description("API REST para gerenciamento de contas bancárias dentro da arquitetura de microserviços");

        Server serve = new Server().url("http://localhost:8080/account-service");
        return new OpenAPI().servers(List.of(serve)).info(info);
    }
}
