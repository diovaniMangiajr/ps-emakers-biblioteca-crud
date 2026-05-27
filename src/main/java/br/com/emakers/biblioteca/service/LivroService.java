package br.com.emakers.biblioteca.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.emakers.biblioteca.domain.Livro;
import br.com.emakers.biblioteca.repository.EmprestimoRepository;
import br.com.emakers.biblioteca.repository.LivroRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

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

        // REGRA DE SEGURANÇA: Verificar se existe qualquer registro pendente na tabela de empréstimos para este livro
        boolean temEmprestimoAtivo = emprestimoRepository.findAll().stream()
            .anyMatch(emprestimo -> emprestimo.getId().getIdLivro().equals(id));

        if (temEmprestimoAtivo) {
            throw new RuntimeException("Não é possível deletar o livro '" + livro.getNome() + "' porque ele possui empréstimos ativos pendentes!");
        }

        livroRepository.delete(livro);
    }
}