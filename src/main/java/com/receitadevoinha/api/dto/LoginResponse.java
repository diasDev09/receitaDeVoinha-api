package com.receitadevoinha.api.dto;

/** Resposta do login: token para o cabecalho Authorization e os dados do usuario. */
public record LoginResponse(String token, UsuarioResponse user) {
}
