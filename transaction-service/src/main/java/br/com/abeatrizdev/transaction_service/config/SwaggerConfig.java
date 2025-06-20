package br.com.abeatrizdev.transaction_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI userOpenAPI() {
        Info info = new Info()
                .title("Banking Transaction Service API")
                .version("1.0")
                .description("API REST para transferência entre de contas bancárias");

        return new OpenAPI().info(info);
    }
}
