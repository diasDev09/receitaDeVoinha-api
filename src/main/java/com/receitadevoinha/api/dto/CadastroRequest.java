package com.receitadevoinha.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Corpo de POST /api/auth/cadastro. Os nomes dos campos são os mesmos do
 * formulário do front, então os erros de "fields" aparecem no campo certo.
 */
public record CadastroRequest(
        @NotBlank(message = "Informe seu nome.")
        @Size(min = 2, max = 80, message = "O nome deve ter entre 2 e 80 caracteres.")
        String name,

        @NotBlank(message = "Escolha um nome de usuário.")
        @Size(min = 3, max = 40, message = "O usuário deve ter entre 3 e 40 caracteres.")
        @Pattern(regexp = "^[A-Za-z0-9._-]*$",
                message = "Use só letras, números, ponto, hífen ou sublinhado, sem espaços.")
        String username,

        @NotBlank(message = "Crie uma senha.")
        // BCrypt considera no máximo 72 bytes da senha
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres.")
        String password) {
}
