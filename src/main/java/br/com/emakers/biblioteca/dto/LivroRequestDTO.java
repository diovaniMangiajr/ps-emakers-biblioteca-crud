package br.com.emakers.biblioteca.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Record DTO para requisição de cadastro de Livros, validando restrições físicas e de estoque.
 */
public record LivroRequestDTO(
    @NotBlank(message = "O nome do livro é obrigatório.")
    @Size(max = 100, message = "O nome do livro não pode ultrapassar 100 caracteres.")
    String nome,

    @NotBlank(message = "O autor do livro é obrigatório.")
    @Size(max = 100, message = "O autor não pode ultrapassar 100 caracteres.")
    String autor,

    @NotNull(message = "A data de lançamento é obrigatória.")
    @PastOrPresent(message = "A data de lançamento não pode ser uma data futura.") // Evita inserção de livros que não foram lançados
    LocalDate dataLancamento,

    @NotNull(message = "A quantidade inicial de livros é obrigatória.")
    @Min(value = 0, message = "A quantidade em estoque não pode ser negativa.") // Garante consistência física do acervo
    Integer quantidade
) {}