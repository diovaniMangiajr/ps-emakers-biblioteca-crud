package br.com.emakers.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

/**
 * Record DTO para requisição de cadastro de Pessoas, blindado com validações do Jakarta Validation.
 */
public record PessoaRequestDTO(
    @NotBlank(message = "O nome é obrigatório e não pode conter apenas espaços.")
    @Size(max = 100, message = "O nome não pode ultrapassar 100 caracteres.")
    String nome,

    @NotBlank(message = "O CPF é obrigatório.")
    @CPF(message = "O CPF informado é inválido.") // Valida o algoritmo de dígitos verificadores do CPF
    String cpf,

    @NotBlank(message = "O CEP é obrigatório.")
    @Size(min = 9, max = 9, message = "O CEP deve estar no formato XXXXX-XXX (9 caracteres).")
    String cep,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail informado deve ser um endereço válido.")
    @Size(max = 100, message = "O e-mail não pode ultrapassar 100 caracteres.")
    String email,

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, max = 100, message = "A senha deve conter entre 6 e 100 caracteres.")
    String senha
) {}