package br.com.emakers.biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import br.com.emakers.biblioteca.domain.Emprestimo;
import br.com.emakers.biblioteca.dto.EmprestimoAtivoResponseDTO;
import br.com.emakers.biblioteca.dto.EmprestimoRequestDTO;
import br.com.emakers.biblioteca.dto.LivroResponseDTO;
import br.com.emakers.biblioteca.service.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
@Tag(name = "Empréstimos", description = "Endpoints para gerenciamento de fluxos de locação e relatórios")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @Operation(summary = "Realizar empréstimo", description = "Registra um novo empréstimo de livro no sistema, validando se há estoque disponível para leitura local.")
    public ResponseEntity<Emprestimo> pegarEmprestado(@RequestBody EmprestimoRequestDTO dto) {
        // Chame a lógica de negócio do Service passando os IDs do DTO
        Emprestimo emprestimoSalvo = emprestimoService.realizarEmprestimo(dto.idLivro(), dto.idPessoa());
        
        // Retorna 201 Created com o registro de empréstimo gerado
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoSalvo);
    }

    @DeleteMapping("/devolver")
    @Operation(summary = "Realizar devolução", description = "Remove o registro de empréstimo e incrementa o estoque do livro de forma atômica no banco de dados.")
    public ResponseEntity<Void> devolverLivro(@RequestBody EmprestimoRequestDTO dto) {
        // Executa a lógica de devolução e atualização de estoque no Service
        emprestimoService.realizarDevolucao(dto.idLivro(), dto.idPessoa());
        
        // Retorna 204 No Content indicando que a devolução (deleção do registro) foi feita com sucesso
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint de Relatório Geral: Lista todos os empréstimos correntes no sistema.
     * Mapeia os dados relacionais complexos para o formato plano do EmprestimoAtivoResponseDTO.
     */
    @GetMapping("/ativos")
    @Operation(summary = "Relatório de empréstimos ativos", description = "Retorna uma listagem achatada e limpa de todos os empréstimos correntes no sistema.")
    public ResponseEntity<List<EmprestimoAtivoResponseDTO>> listarTodosAtivos() {
        List<EmprestimoAtivoResponseDTO> ativos = emprestimoService.buscarTodosEmprestimosAtivos().stream()
                .map(e -> new EmprestimoAtivoResponseDTO(
                        e.getLivro().getIdLivro(),     // Extrai o ID do livro associado
                        e.getLivro().getNome(),        // Extrai o Nome do livro associado
                        e.getPessoa().getIdPessoa(),   // Extrai o ID da pessoa associada
                        e.getPessoa().getNome()        // Extrai o Nome da pessoa associada
                )).toList();
        return ResponseEntity.ok(ativos);
    }

    /**
     * Endpoint de Relatório Específico: Lista todos os livros que pertencem a uma pessoa.
     * Recebe o ID dinamicamente pela URL através do @PathVariable.
     */
    @GetMapping("/pessoa/{idPessoa}")
    @Operation(summary = "Listar livros por pessoa", description = "Retorna todos os livros que estão atualmente sob a posse de uma pessoa específica.")
    public ResponseEntity<List<LivroResponseDTO>> listarPorPessoa(@PathVariable Integer idPessoa) {
        List<LivroResponseDTO> livros = emprestimoService.buscarLivrosEmprestadosPorPessoa(idPessoa).stream()
                .map(l -> new LivroResponseDTO(l.getIdLivro(), l.getNome(), l.getAutor(), l.getDataLancamento(), l.getQuantidade()))
                .toList();
        return ResponseEntity.ok(livros);
    }
}