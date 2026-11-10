package com.receitadevoinha.api.service;

import com.receitadevoinha.api.dto.CategoriaResponse;
import com.receitadevoinha.api.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repo;

    public CategoriaService(CategoriaRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repo.findAll().stream().map(CategoriaResponse::from).toList();
    }
}
