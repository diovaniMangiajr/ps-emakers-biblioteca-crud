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

    // Captura erros de JSON malformado ou tipos de dados errados no corpo da requisição
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return criarErroResponse(HttpStatus.BAD_REQUEST, "O corpo da requisição JSON possui erros de formatação ou tipos de dados inválidos.");
    }

    // Captura erros de digitação na URL (ex: passar texto onde se espera o ID numérico)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        // Isola o retorno em uma variável local para zerar os warnings de fluxo do compilador
        Class<?> tipoRequerido = ex.getRequiredType();
        String tipoEsperado = (tipoRequerido != null) ? tipoRequerido.getSimpleName() : "especificado";
        
        String mensagem = String.format("O parâmetro '%s' preenchido na URL deve ser do tipo %s.", ex.getName(), tipoEsperado);
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
     * Transforma um erro complexo de framework em um JSON limpo e legível (HTTP 400).
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String mensagemErro = (error.getDefaultMessage() != null) ? error.getDefaultMessage() : "Valor inválido";
            sb.append(error.getField()).append(": ").append(mensagemErro).append(" | ");
        });
        
        String mensagemFormatada = sb.toString();
        if (mensagemFormatada.endsWith(" | ")) {
            mensagemFormatada = mensagemFormatada.substring(0, mensagemFormatada.length() - 3);
        }

        return criarErroResponse(HttpStatus.BAD_REQUEST, mensagemFormatada);
    }

    // Captura erros lógicos criados no Service
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponseDTO> handleRuntimeException(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Erro inesperado no servidor.";
        
        // Evita NullPointerException e ignora erros internos de mapeamento do OpenAPI
        if (ex.getClass().getName().contains("springdoc") || msg.contains("OpenAPI")) {
            throw ex;
        }

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (msg.toLowerCase().contains("não encontrado") || msg.toLowerCase().contains("não encontrada")) {
            status = HttpStatus.NOT_FOUND;
        }

        return criarErroResponse(status, msg);
    }
}