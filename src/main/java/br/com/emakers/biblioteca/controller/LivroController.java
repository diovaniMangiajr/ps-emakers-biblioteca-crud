package br.com.emakers.biblioteca.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emakers.biblioteca.domain.Livro;
import br.com.emakers.biblioteca.dto.LivroRequestDTO;
import br.com.emakers.biblioteca.dto.LivroResponseDTO;
import br.com.emakers.biblioteca.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
@Tag(name = "Livros", description = "Endpoints para gerenciamento do acervo técnico e buscas avançadas")
public class LivroController {

    private final LivroService livroService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo livro", description = "Insere uma obra no acervo, validando restrições de obrigatoriedade e estoque mínimo não-negativo.")
    public ResponseEntity<Livro> cadastrarLivro(@jakarta.validation.Valid @RequestBody LivroRequestDTO dto) {
        // Convertemos o DTO recebido da web para a nossa Entidade JPA
        Livro novoLivro = new Livro();
        novoLivro.setNome(dto.nome());
        novoLivro.setAutor(dto.autor());
        novoLivro.setDataLancamento(dto.dataLancamento());
        novoLivro.setQuantidade(dto.quantidade());

        Livro livroSalvo = livroService.salvar(novoLivro);
        
        // Retorna HTTP Status 211 (Created) com o objeto salvo no corpo
        return ResponseEntity.status(HttpStatus.CREATED).body(livroSalvo);
    }

    @GetMapping
    @Operation(summary = "Listar todos os livros", description = "Retorna a listagem completa de livros salvos na base de dados.")
    public ResponseEntity<List<Livro>> listarTodos() {
        List<Livro> livros = livroService.buscarTodos();
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar livro por ID", description = "Retorna os detalhes de um livro específico através do identificador numérico.")
    public ResponseEntity<Livro> buscarPorId(@PathVariable Integer id) {
        Livro livro = livroService.buscarPorId(id);
        return ResponseEntity.ok(livro);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um livro", description = "Exclui fisicamente um livro se ele não possuir nenhum empréstimo ativo pendente.")
    public ResponseEntity<Void> deletarLivro(@PathVariable Integer id) {
        livroService.deletar(id);
        // Retorna HTTP Status 204 (No Content), padrão de mercado para deleções bem-sucedidas
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para busca avançada de livros por parte do nome.
     * Usa Stream API para mapear de forma performática a Entidade para o ResponseDTO.
     */
    @GetMapping("/buscar-por-nome")
    @Operation(summary = "Filtrar livros por nome", description = "Busca avançada por partes do nome utilizando regras de case-insensitive.")
    public ResponseEntity<List<LivroResponseDTO>> buscarPorNome(@org.springframework.web.bind.annotation.RequestParam String nome) {
        List<LivroResponseDTO> livros = livroService.buscarPorNome(nome).stream()
                .map(l -> new LivroResponseDTO(l.getIdLivro(), l.getNome(), l.getAutor(), l.getDataLancamento(), l.getQuantidade()))
                .toList(); // Converte o fluxo da stream de volta para uma lista imutável
        return ResponseEntity.ok(livros); // Retorna HTTP 200 OK com o payload convertido
    }

    /**
     * Endpoint para busca avançada de livros filtrados pelo autor.
     */
    @GetMapping("/buscar-por-autor")
    @Operation(summary = "Filtrar livros por autor", description = "Busca avançada por partes do nome do autor com ignorância de maiúsculas e minúsculas.")
    public ResponseEntity<List<LivroResponseDTO>> buscarPorAutor(@org.springframework.web.bind.annotation.RequestParam String autor) {
        List<LivroResponseDTO> livros = livroService.buscarPorAutor(autor).stream()
                .map(l -> new LivroResponseDTO(l.getIdLivro(), l.getNome(), l.getAutor(), l.getDataLancamento(), l.getQuantidade()))
                .toList();
        return ResponseEntity.ok(livros);
    }
}