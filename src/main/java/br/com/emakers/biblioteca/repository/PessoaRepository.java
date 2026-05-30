package br.com.emakers.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.emakers.biblioteca.domain.Pessoa;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
    // O JpaRepository exige a Entidade (Pessoa) e o tipo da Chave Primária (Integer)
    java.util.Optional<Pessoa> findByEmail(String email);
}