package br.com.emakers.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.emakers.biblioteca.domain.Emprestimo;
import br.com.emakers.biblioteca.domain.EmprestimoId;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, EmprestimoId> {
    // Nota: Aqui passamos a nossa classe de chave composta (EmprestimoId)
}