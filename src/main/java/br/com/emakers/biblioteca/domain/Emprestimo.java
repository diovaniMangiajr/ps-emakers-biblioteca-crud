package br.com.emakers.biblioteca.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Emprestimo")
@Getter
@Setter
@NoArgsConstructor

public class Emprestimo{

    @EmbeddedId
    private EmprestimoId id = new EmprestimoId();

    @ManyToOne
    @MapsId("idLivro") // Diz ao JPA que esse relacionamento preenche o "idLivro" da chave composta
    @JoinColumn(name = "idLivro")
    private Livro livro;

    @ManyToOne
    @MapsId("idPessoa")
    @JoinColumn(name = "idPessoa")
    private Pessoa pessoa;

    public Emprestimo(Livro livro, Pessoa pessoa) {
        this.livro = livro;
        this.pessoa = pessoa;
        this.id = new EmprestimoId(livro.getIdLivro(), pessoa.getIdPessoa());
    }
}
