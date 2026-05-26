package br.com.emakers.biblioteca.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.emakers.biblioteca.domain.Livro;
import br.com.emakers.biblioteca.repository.LivroRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    public Livro salvar(Livro livro) {
        return livroRepository.save(livro);
    }

    public List<Livro> buscarTodos() {
        return livroRepository.findAll();
    }

    public Livro buscarPorId(Integer id) {
        return livroRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Livro não encontrado com o ID: " + id));
    }

    public void deletar(Integer id) {
        Livro livro = buscarPorId(id);
        livroRepository.delete(livro);
    }
}