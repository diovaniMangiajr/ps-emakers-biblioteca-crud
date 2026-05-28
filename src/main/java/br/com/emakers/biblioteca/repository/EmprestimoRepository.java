package br.com.emakers.biblioteca.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.com.emakers.biblioteca.domain.Emprestimo;
import br.com.emakers.biblioteca.domain.EmprestimoId;
import br.com.emakers.biblioteca.domain.Livro;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, EmprestimoId> {

    /**
     * Consulta JPQL customizada: Seleciona apenas a entidade 'Livro' associada aos empréstimos
     * filtrando pelo ID da pessoa. Navegamos na chave composta usando 'e.id.idPessoa'.
     */
    @Query("SELECT e.livro FROM Emprestimo e WHERE e.id.idPessoa = :idPessoa")
    List<Livro> findLivrosByPessoaId(@Param("idPessoa") Integer idPessoa);
    
    /**
     * Derived Query utilizando a estrutura do Embedded ID:
     * Verifica no banco se existe algum registro de empréstimo ativo com o ID do livro informado.
     * Crucial para a regra de negócio que impede a exclusão de livros vinculados a empréstimos.
     */
    boolean existsByIdIdLivro(Integer idLivro);
}