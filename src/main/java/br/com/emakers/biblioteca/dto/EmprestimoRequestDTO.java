package br.com.emakers.biblioteca.dto;

public record EmprestimoRequestDTO(
    Integer idLivro,
    Integer idPessoa
) {
}