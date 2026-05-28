package br.com.emakers.biblioteca.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.emakers.biblioteca.domain.Livro;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Integer> {
    /**
     * Derived Query: Busca livros onde o nome contenha o termo informado,
     * ignorando maiúsculas e minúsculas (equivalente ao ILIKE %termo% do SQL).
     */
    List<Livro> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Derived Query: Busca livros pelo nome do autor contendo o termo informado,
     * também aplicando o 'IgnoreCase' para flexibilidade na busca.
     */
    List<Livro> findByAutorContainingIgnoreCase(String autor);
}