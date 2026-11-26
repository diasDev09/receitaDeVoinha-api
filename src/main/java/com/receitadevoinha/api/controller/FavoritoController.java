package com.receitadevoinha.api.controller;

import com.receitadevoinha.api.auth.AuthInterceptor;
import com.receitadevoinha.api.auth.RequerLogin;
import com.receitadevoinha.api.dto.ReceitaResponse;
import com.receitadevoinha.api.service.FavoritoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Todos os endpoints de favoritos exigem login: cada usuario tem os seus. */
@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    @RequerLogin
    @GetMapping
    public List<ReceitaResponse> listar(@RequestAttribute(AuthInterceptor.ATRIBUTO_USUARIO) Long usuarioId) {
        return service.listar(usuarioId);
    }

    @RequerLogin
    @PutMapping("/{receitaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adicionar(
            @PathVariable Long receitaId,
            @RequestAttribute(AuthInterceptor.ATRIBUTO_USUARIO) Long usuarioId) {
        service.adicionar(usuarioId, receitaId);
    }

    @RequerLogin
    @DeleteMapping("/{receitaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(
            @PathVariable Long receitaId,
            @RequestAttribute(AuthInterceptor.ATRIBUTO_USUARIO) Long usuarioId) {
        service.remover(usuarioId, receitaId);
    }
}
