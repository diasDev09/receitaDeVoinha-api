package com.receitadevoinha.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Corpo de POST /api/receitas. Os nomes dos campos sao os mesmos do formulario
 * do front, entao os erros de "fields" aparecem direto no campo certo.
 * As regras repetem as de validateRecipe (src/utils/validators.js no front).
 */
public record ReceitaRequest(
        @NotBlank(message = "Informe o nome da receita.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String title,

        @NotBlank(message = "Selecione uma categoria.")
        String category,

        @NotBlank(message = "Informe o tempo de preparo (ex.: 40min).")
        @Size(max = 20, message = "Use no máximo 20 caracteres.")
        String time,

        @NotBlank(message = "Informe a descrição.")
        @Size(min = 10, max = 1000, message = "A descrição deve ter entre 10 e 1000 caracteres.")
        String description,

        @Size(max = 255, message = "Use no máximo 255 caracteres.")
        @Pattern(regexp = "^$|^(https?://|/).*", message = "Use um link (http...) ou um caminho começando com /.")
        String image,

        @NotNull(message = "Informe os ingredientes.")
        @Size(min = 2, message = "Informe pelo menos 2 ingredientes (um por linha).")
        List<@NotBlank(message = "Ingrediente vazio.") @Size(max = 255) String> ingredients,

        @NotNull(message = "Informe o modo de preparo.")
        @Size(min = 1, message = "Informe pelo menos 1 passo (um por linha).")
        List<@NotBlank(message = "Passo vazio.") @Size(max = 1000) String> preparation) {
}
