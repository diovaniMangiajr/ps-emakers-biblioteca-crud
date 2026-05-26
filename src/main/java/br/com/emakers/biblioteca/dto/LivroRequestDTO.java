package br.com.emakers.biblioteca.dto;

import java.time.LocalDate;

public record LivroRequestDTO(
    String nome,
    String autor,
    LocalDate dataLancamento,
    Integer quantidade
) {
}