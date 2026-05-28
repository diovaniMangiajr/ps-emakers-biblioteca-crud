package br.com.emakers.biblioteca.dto;

/**
 * DTO para achatar (flatten) e simplificar a estrutura de Empréstimos ativos.
 * Transforma o relacionamento complexo M:N em uma resposta JSON direta e legível,
 * omitindo dados sensíveis como a senha da Pessoa.
 */
public record EmprestimoAtivoResponseDTO(
    Integer idLivro,
    String nomeLivro,
    Integer idPessoa,
    String nomePessoa
) {}