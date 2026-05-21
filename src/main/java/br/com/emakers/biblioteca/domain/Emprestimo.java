package br.com.emakers.biblioteca.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Emprestimo {

    @EmbeddedId
    private EmprestimoId id = new EmprestimoId();

    @ManyToOne
    @MapsId("idLivro") // Aponta para a variável no Java (EmprestimoId.java)
    @JoinColumn(name = "id_livro") // Aponta para a coluna física no Postgres
    private Livro livro;

    @ManyToOne
    @MapsId("idPessoa") // Aponta para a variável no Java (EmprestimoId.java)
    @JoinColumn(name = "id_pessoa") // Aponta para a coluna física no Postgres
    private Pessoa pessoa;

    public Emprestimo(Livro livro, Pessoa pessoa) {
        this.livro = livro;
        this.pessoa = pessoa;
        this.id = new EmprestimoId(livro.getIdLivro(), pessoa.getIdPessoa());
    }
}