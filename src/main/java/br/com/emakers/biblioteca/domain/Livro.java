package br.com.emakers.biblioteca.domain;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Livro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Livro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLivro")
    private Integer idLivro;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "autor", nullable = false, length = 100)
    private String autor;

    @Column(name = "data_lancamento")
    private LocalDate dataLancamento;
}
