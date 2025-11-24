package ru.globus.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Swagger/OpenAPI
 *
 * @author Vladlen Korablev
 */
@Configuration
public class SwaggerConfig {
    /**
     * Создает описание API.
     *
     * @return объект OpenAPI с метаданными сервиса
     *
     * @author Vladlen Korablev
     */
    @Bean
    public OpenAPI waiterServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Globus Project API")
                .version("1.0.0")
                .description("""
                    REST API для управления 
                    """)
                .contact(new Contact()
                    .name("Vladlen Korablev")
                    .email("korablev.vlm@gmail.com")
                    .url("https://github.com/edlor-k/GlobusLabs"))
            );
    }
}
