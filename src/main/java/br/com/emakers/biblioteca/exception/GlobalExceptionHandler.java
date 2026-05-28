package br.com.emakers.biblioteca.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.com.emakers.biblioteca.dto.ErroResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura erros lógicos que criados no Service
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponseDTO> handleRuntimeException(RuntimeException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex.getMessage().toLowerCase().contains("não encontrado") || ex.getMessage().toLowerCase().contains("não encontrada")) {
            status = HttpStatus.NOT_FOUND;
        }

        return criarErroResponse(status, ex.getMessage());
    }

    // Captura erros de JSON malformado ou tipos de dados errados no corpo da requisição
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return criarErroResponse(HttpStatus.BAD_REQUEST, "O corpo da requisição JSON possui erros de formatação ou tipos de dados inválidos.");
    }

    // Captura erros de digitação na URL (ex: passar texto onde se espera o ID numérico)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String mensagem = String.format("O parâmetro '%s' preenchido na URL deve ser do tipo %s.", ex.getName(), ex.getRequiredType().getSimpleName());
        return criarErroResponse(HttpStatus.BAD_REQUEST, mensagem);
    }

    // Captura violações de regras do banco de dados (ex: CPF ou Email duplicados)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponseDTO> handleDataIntegrity(DataIntegrityViolationException ex) {
        return criarErroResponse(HttpStatus.CONFLICT, "Erro de integridade de dados: Um registro com dados únicos informados já existe no sistema.");
    }

    // Método auxiliar para evitar repetição de código (Clean Code!)
    private ResponseEntity<ErroResponseDTO> criarErroResponse(HttpStatus status, String mensagem) {
        ErroResponseDTO erroResponse = new ErroResponseDTO(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            mensagem
        );
        return ResponseEntity.status(status).body(erroResponse);
    }

    /**
     * Captura erros de validação (@Valid) nos payloads de entrada da API.
     * Transforma um erro complexo de framework em um JSON limpo e legível (HTTP 400)[cite: 29, 34].
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        // Instancia um StringBuilder para agrupar todas as mensagens de erro caso o JSON venha com múltiplos campos inválidos
        StringBuilder sb = new StringBuilder();
        
        // Percorre a lista de erros de campo gerados pelo Spring Validator
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(" | ");
        });
        
        String mensagemFormatada = sb.toString();
        // Remove os caracteres residuais " | " do final da string para estética do payload
        if (mensagemFormatada.endsWith(" | ")) {
            mensagemFormatada = mensagemFormatada.substring(0, mensagemFormatada.length() - 3);
        }

        // Retorna HTTP Status 400 (Bad Request) encapsulado no ErroResponseDTO padrão do projeto [cite: 30, 34]
        return criarErroResponse(HttpStatus.BAD_REQUEST, mensagemFormatada);
    }
}