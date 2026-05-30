package br.com.emakers.biblioteca.service;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import br.com.emakers.biblioteca.domain.Pessoa;

@Service
public class TokenService {

    // Define uma chave secreta padrão caso não encontre no application.properties
    @Value("${api.security.token.secret:emakers-secret-key-12345}")
    private String secret;

    /**
     * Gera o Token JWT para o usuário autenticado.
     */
    public String gerarToken(Pessoa pessoa) {
        try {
            // Define o algoritmo HMAC256 passando a assinatura secreta
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            
            return JWT.create()
                    .withIssuer("biblioteca-api") // Emissor do Token
                    .withSubject(pessoa.getEmail()) // Identificador único (E-mail)
                    .withExpiresAt(gerarDataExpiracao()) // Tempo de vida do Token
                    .sign(algoritmo); // Aplica a assinatura digital
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar o token de autenticação JWT.", exception);
        }
    }

    /**
     * Define o tempo de expiração do Token para 2 horas (fuso horário do Brasil -03:00).
     */
    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    /**
     * Valida o token e extrai o e-mail (Subject) de dentro dele.
     */
    public String validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("biblioteca-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return ""; // Se o token for inválido, expirado ou adulterado, retorna vazio
        }
    }
}