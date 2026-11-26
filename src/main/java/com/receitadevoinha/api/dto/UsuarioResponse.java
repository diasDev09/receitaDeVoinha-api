package com.receitadevoinha.api.dto;

import com.receitadevoinha.api.model.Usuario;

/** Dados publicos do usuario. Nunca inclui a senha nem o hash. */
public record UsuarioResponse(Long id, String name, String username) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getLogin());
    }
}
