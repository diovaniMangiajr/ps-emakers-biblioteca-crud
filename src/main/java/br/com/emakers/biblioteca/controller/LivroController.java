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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;

    @PostMapping
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
    public ResponseEntity<List<Livro>> listarTodos() {
        List<Livro> livros = livroService.buscarTodos();
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarPorId(@PathVariable Integer id) {
        Livro livro = livroService.buscarPorId(id);
        return ResponseEntity.ok(livro);
    }

    @DeleteMapping("/{id}")
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
    public ResponseEntity<List<LivroResponseDTO>> buscarPorAutor(@org.springframework.web.bind.annotation.RequestParam String autor) {
        List<LivroResponseDTO> livros = livroService.buscarPorAutor(autor).stream()
                .map(l -> new LivroResponseDTO(l.getIdLivro(), l.getNome(), l.getAutor(), l.getDataLancamento(), l.getQuantidade()))
                .toList();
        return ResponseEntity.ok(livros);
    }
}