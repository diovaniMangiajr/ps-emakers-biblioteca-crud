package br.com.emakers.biblioteca.dto;

/**
 * Record DTO para mapear o retorno da API externa do ViaCep.
 * O Spring mapeará as chaves do JSON automaticamente para estes campos.
 */
public record ViaCepResponseDTO(
    String cep,
    String logradouro,
    String complemento,
    String bairro,
    String localidade,
    String uf
) {}