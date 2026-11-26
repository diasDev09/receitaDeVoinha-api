package com.receitadevoinha.api.dto;

import com.receitadevoinha.api.model.Receita;
import com.receitadevoinha.api.model.Usuario;

import java.util.List;

/**
 * Mantem os nomes de campo do modelo da AV1 (recipes.js) para o front
 * continuar funcionando sem reescrita. authorId/author foram acrescentados
 * na AV2 para o front saber quem pode excluir a receita.
 */
public record ReceitaResponse(
        Long id,
        String title,
        String category,
        String description,
        String image,
        double rating,
        String time,
        List<String> ingredients,
        List<String> preparation,
        Long authorId,
        String author) {

    public static ReceitaResponse from(Receita receita) {
        Usuario autor = receita.getAutor();
        return new ReceitaResponse(
                receita.getId(),
                receita.getTitulo(),
                receita.getCategoria().getNome(),
                receita.getDescricao(),
                receita.getImagem(),
                receita.getNota(),
                receita.getTempo(),
                List.copyOf(receita.getIngredientes()),
                List.copyOf(receita.getPreparo()),
                autor == null ? null : autor.getId(),
                autor == null ? null : autor.getNome());
    }
}
