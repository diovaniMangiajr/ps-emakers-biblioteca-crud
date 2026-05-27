package br.com.emakers.biblioteca.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.emakers.biblioteca.dto.ErroResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Este método captura especificamente a 'RuntimeException' que lançamos nos Services
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponseDTO> handleRuntimeException(RuntimeException ex) {
        
        HttpStatus status = HttpStatus.BAD_REQUEST; // Status 400

        // Se a mensagem contiver "não encontrado", podemos ser mais específicos e mandar um 404
        if (ex.getMessage().toLowerCase().contains("não encontrado") || ex.getMessage().toLowerCase().contains("não encontrada")) {
            status = HttpStatus.NOT_FOUND; // Status 404
        }

        ErroResponseDTO erroResponse = new ErroResponseDTO(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage()
        );

        return ResponseEntity.status(status).body(erroResponse);
    }
}