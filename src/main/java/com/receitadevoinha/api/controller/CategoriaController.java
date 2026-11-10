package com.receitadevoinha.api.controller;

import com.receitadevoinha.api.dto.CategoriaResponse;
import com.receitadevoinha.api.service.CategoriaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping // GET /api/categorias
    public List<CategoriaResponse> listar() {
        return service.listar();
    }
}
