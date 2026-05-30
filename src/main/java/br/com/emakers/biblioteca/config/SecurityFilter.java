package br.com.emakers.biblioteca.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.emakers.biblioteca.domain.Pessoa;
import br.com.emakers.biblioteca.repository.PessoaRepository;
import br.com.emakers.biblioteca.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final PessoaRepository pessoaRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Tenta extrair o token do cabeçalho da requisição
        var token = this.recuperarToken(request);
        
        if (token != null) {
            // Se existe token, tenta validar e extrair o e-mail
            var email = tokenService.validarToken(token);
            
            if (!email.isEmpty()) {
                // Busca o usuário no banco para garantir que ele ainda existe
                Pessoa pessoa = pessoaRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
                
                // Cria o objeto de autenticação e força o Spring a logar o usuário na requisição atual
                var authentication = new UsernamePasswordAuthenticationToken(pessoa, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        // Continua o fluxo da requisição (vai para o próximo filtro ou para o Controller)
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}