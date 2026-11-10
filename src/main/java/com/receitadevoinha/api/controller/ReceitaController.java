package com.receitadevoinha.api.controller;

import com.receitadevoinha.api.dto.ReceitaResponse;
import com.receitadevoinha.api.service.ReceitaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/receitas")
public class ReceitaController {

    private final ReceitaService service;

    public ReceitaController(ReceitaService service) {
        this.service = service;
    }

    @GetMapping // GET /api/receitas?busca=queijo&categoria=Massas
    public List<ReceitaResponse> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String categoria) {
        return service.listar(busca, categoria);
    }

    @GetMapping("/{id}") // GET /api/receitas/1
    public ReceitaResponse detalhar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
