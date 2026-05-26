package br.com.emakers.biblioteca.dto;

public record PessoaRequestDTO(
    String nome,
    String cpf,
    String cep,
    String email,
    String senha
) {
}