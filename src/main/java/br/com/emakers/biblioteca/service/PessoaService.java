package br.com.emakers.biblioteca.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.emakers.biblioteca.domain.Pessoa;
import br.com.emakers.biblioteca.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    public Pessoa salvar(Pessoa pessoa) {
        // Futuramente podemos adicionar criptografia de senha ou validação de CPF aqui
        return pessoaRepository.save(pessoa);
    }

    public List<Pessoa> buscarTodas() {
        return pessoaRepository.findAll();
    }

    public Pessoa buscarPorId(Integer id) {
        return pessoaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pessoa não encontrada com o ID: " + id));
    }

    public void deletar(Integer id) {
        Pessoa pessoa = buscarPorId(id);
        pessoaRepository.delete(pessoa);
    }
}