package br.com.emakers.biblioteca.dto;

import java.time.LocalDate;
/**
 * DTO Imutável (Java Record) para transporte de dados de Livros na resposta da API.
 * Evita a exposição direta da entidade JPA 'Livro' na camada Web.
 */
public record LivroResponseDTO(
    Integer idLivro,
    String nome,
    String autor,
    LocalDate dataLancamento,
    Integer quantidade
) {}