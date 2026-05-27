package br.com.emakers.biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emakers.biblioteca.domain.Emprestimo;
import br.com.emakers.biblioteca.dto.EmprestimoRequestDTO;
import br.com.emakers.biblioteca.service.EmprestimoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping("/pegar")
    public ResponseEntity<Emprestimo> pegarEmprestado(@RequestBody EmprestimoRequestDTO dto) {
        // Chame a lógica de negócio do Service passando os IDs do DTO
        Emprestimo emprestimoSalvo = emprestimoService.realizarEmprestimo(dto.idLivro(), dto.idPessoa());
        
        // Retorna 201 Created com o registro de empréstimo gerado
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoSalvo);
    }

    @DeleteMapping("/devolver")
    public ResponseEntity<Void> devolverLivro(@RequestBody EmprestimoRequestDTO dto) {
        // Executa a lógica de devolução e atualização de estoque no Service
        emprestimoService.realizarDevolucao(dto.idLivro(), dto.idPessoa());
        
        // Retorna 204 No Content indicando que a devolução (deleção do registro) foi feita com sucesso
        return ResponseEntity.noContent().build();
    }
}