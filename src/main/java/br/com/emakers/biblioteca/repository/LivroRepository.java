package br.com.emakers.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.emakers.biblioteca.domain.Livro;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Integer> {
}