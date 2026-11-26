package com.receitadevoinha.api.dto;

import jakarta.validation.constraints.NotBlank;

/** Corpo de POST /api/auth/login: { "username": "voinha", "password": "..." } */
public record LoginRequest(
        @NotBlank(message = "Informe o usuário.") String username,
        @NotBlank(message = "Informe a senha.") String password) {
}
