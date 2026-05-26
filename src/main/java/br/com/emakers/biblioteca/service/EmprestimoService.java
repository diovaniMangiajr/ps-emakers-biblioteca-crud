package br.com.emakers.biblioteca.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.emakers.biblioteca.domain.Emprestimo;
import br.com.emakers.biblioteca.domain.EmprestimoId;
import br.com.emakers.biblioteca.domain.Livro;
import br.com.emakers.biblioteca.domain.Pessoa;
import br.com.emakers.biblioteca.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroService livroService;
    private final PessoaService pessoaService;

    @Transactional
    public Emprestimo realizarEmprestimo(Integer idLivro, Integer idPessoa) {
        // Validar se o Livro existe
        Livro livro = livroService.buscarPorId(idLivro);

        // Validar se a Pessoa existe
        Pessoa pessoa = pessoaService.buscarPorId(idPessoa);

        // Criar a chave composta para verificação
        EmprestimoId emprestimoId = new EmprestimoId(idLivro, idPessoa);

        // Verificar se esta pessoa já está com este livro específico
        if (emprestimoRepository.existsById(emprestimoId)) {
            throw new RuntimeException("Este livro já está emprestado para esta pessoa!");
        }

        // NOVA REGRA: Verificar se há exemplares disponíveis no estoque
        // Deve haver um livro para leitura dentro da biblioteca
        if (livro.getQuantidade() <= 1) {
            throw new RuntimeException("Não há exemplares disponíveis do livro '" + livro.getNome() + "' para empréstimo!");
        }

        // Decrementar a quantidade do estoque do livro e atualizar
        livro.setQuantidade(livro.getQuantidade() - 1);
        livroService.salvar(livro); // Salva o livro com o estoque atualizado

        // Criar o registro de Empréstimo
        Emprestimo emprestimo = new Emprestimo(livro, pessoa);
        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public void realizarDevolucao(Integer idLivro, Integer idPessoa) {
        // Montar a chave composta do empréstimo que será devolvido
        EmprestimoId emprestimoId = new EmprestimoId(idLivro, idPessoa);

        // Regra de Negócio: Verificar se o empréstimo realmente existe no banco
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
            .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado para o Livro ID " + idLivro + " e Pessoa ID " + idPessoa));

        // Deletar o registro (o que significa que o livro foi devolvido)
        emprestimoRepository.delete(emprestimo);
    }
}