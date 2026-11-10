package com.receitadevoinha.api.repository;

import com.receitadevoinha.api.model.Receita;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceitaRepository extends JpaRepository<Receita, Long> {
}
