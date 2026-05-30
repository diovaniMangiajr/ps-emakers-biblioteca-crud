package br.com.emakers.biblioteca.dto;

public record LoginResponseDTO(
    String email,
    String token
) {}