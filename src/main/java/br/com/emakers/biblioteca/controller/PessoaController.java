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

import br.com.emakers.biblioteca.domain.Pessoa;
import br.com.emakers.biblioteca.dto.PessoaRequestDTO;
import br.com.emakers.biblioteca.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
@Tag(name = "Pessoas", description = "Endpoints para gerenciamento de dados de usuários e integração de CEP")
public class PessoaController {

    private final PessoaService pessoaService;

    @PostMapping
    @Operation(summary = "Cadastrar uma pessoa", description = "Cadastra um usuário validando a estrutura de e-mail, algoritmo de CPF e consulta síncrona do CEP via ViaCep.")
    public ResponseEntity<Pessoa> cadastrarPessoa(@jakarta.validation.Valid @RequestBody PessoaRequestDTO dto) {
        // Convertemos o Record DTO recebido da Web para a Entidade JPA Pessoa
        Pessoa novaPessoa = new Pessoa();
        novaPessoa.setNome(dto.nome());
        novaPessoa.setCpf(dto.cpf());
        novaPessoa.setCep(dto.cep());
        novaPessoa.setEmail(dto.email());
        novaPessoa.setSenha(dto.senha()); // Futuramente, posso aplicar hash aqui

        Pessoa pessoaSalva = pessoaService.salvar(novaPessoa);
        
        // Retorna o HTTP Status 201 (Created) com o objeto salvo
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
    }

    @GetMapping
    @Operation(summary = "Listar todas as pessoas", description = "Retorna a lista completa de usuários cadastrados no sistema.")
    public ResponseEntity<List<Pessoa>> listarTodas() {
        List<Pessoa> pessoas = pessoaService.buscarTodas();
        return ResponseEntity.ok(pessoas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pessoa por ID", description = "Retorna os dados cadastrais de um usuário específico filtrado pelo ID.")
    public ResponseEntity<Pessoa> buscarPorId(@PathVariable Integer id) {
        Pessoa pessoa = pessoaService.buscarPorId(id);
        return ResponseEntity.ok(pessoa);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma pessoa", description = "Remove o registro do usuário se ele não possuir livros pendentes de devolução.")
    public ResponseEntity<Void> deletarPessoa(@PathVariable Integer id) {
        pessoaService.deletar(id);
        // Retorna o HTTP Status 204 (No Content) indicando sucesso na deleção
        return ResponseEntity.noContent().build();
    }
}