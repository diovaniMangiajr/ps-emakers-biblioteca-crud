package br.com.emakers.biblioteca.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import br.com.emakers.biblioteca.domain.Pessoa;
import br.com.emakers.biblioteca.dto.LoginRequestDTO;
import br.com.emakers.biblioteca.dto.LoginResponseDTO;
import br.com.emakers.biblioteca.repository.PessoaRepository;
import br.com.emakers.biblioteca.service.TokenService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoint para login de usuários e obtenção do Token JWT")
public class AuthController {

    private final PessoaRepository pessoaRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    @Operation(summary = "Efetuar login", description = "Valida as credenciais do usuário e retorna o Token JWT de acesso para rotas protegidas.")
    public ResponseEntity<LoginResponseDTO> login(@jakarta.validation.Valid @RequestBody LoginRequestDTO dto) {
        
        // Busca a pessoa pelo e-mail informado
        Pessoa pessoa = pessoaRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("E-mail ou senha incorretos."));

        // Compara a senha digitada em texto limpo com o hash BCrypt do banco
        if (!passwordEncoder.matches(dto.senha(), pessoa.getSenha())) {
            throw new RuntimeException("E-mail ou senha incorretos.");
        }

        // Se as credenciais forem válidas, gera o Token JWT
        String token = tokenService.gerarToken(pessoa);

        // Retorna o e-mail e o Token gerado no corpo da resposta (Status 200 OK)
        return ResponseEntity.ok(new LoginResponseDTO(pessoa.getEmail(), token));
    }
}