package br.com.emakers.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

/**
 * Classe de configuracao para customizar os metadados do Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API RESTful de Gerenciamento de Biblioteca")
                        .version("v2.0")
                        .description("Sistema corporativo para controle de acervo, gerenciamento de usuarios, emprestimos e devolucoes de livros.")
                        .contact(new Contact()
                                .name("Processo Trainee - Emakers Jr.")
                                .email("emakers.junior@ufla.br")));
    }
}