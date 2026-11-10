package com.receitadevoinha.api.dto;

import com.receitadevoinha.api.model.Categoria;

public record CategoriaResponse(Long id, String name, String icon) {

    public static CategoriaResponse from(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getIcone());
    }
}
