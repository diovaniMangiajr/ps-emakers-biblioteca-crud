package br.com.emakers.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Classe de configuracao para customizar os metadados do Swagger UI
 * e habilitar a autenticação JWT pela interface.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Mantém os seus metadados originais intactos
                .info(new Info()
                        .title("API RESTful de Gerenciamento de Biblioteca")
                        .version("v2.0")
                        .description("Sistema corporativo para controle de acervo, gerenciamento de usuarios, emprestimos e devolucoes de livros.")
                        .contact(new Contact()
                                .name("Processo Trainee - Emakers Jr.")
                                .email("emakers.junior@ufla.br")))
                
                // Adiciona a exigência global de segurança na documentação
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                
                // Define como o Swagger deve enviar o token (via Header HTTP no formato Bearer)
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .name("BearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}