package br.com.emakers.biblioteca.domain;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
public class Pessoa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPessoa;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @JdbcTypeCode(Types.CHAR)
    @Column(columnDefinition = "char(11)", nullable = false, unique = true)
    private String cpf;
    
    @JdbcTypeCode(Types.CHAR)
    @Column(columnDefinition = "char(9)")
    private String cep;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, length = 100)
    private String senha;
}