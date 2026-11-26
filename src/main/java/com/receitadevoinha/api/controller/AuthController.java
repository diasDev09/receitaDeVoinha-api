package com.receitadevoinha.api.controller;

import com.receitadevoinha.api.auth.AuthInterceptor;
import com.receitadevoinha.api.auth.RequerLogin;
import com.receitadevoinha.api.dto.CadastroRequest;
import com.receitadevoinha.api.dto.LoginRequest;
import com.receitadevoinha.api.dto.LoginResponse;
import com.receitadevoinha.api.dto.UsuarioResponse;
import com.receitadevoinha.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login") // 200 com token; 401 se usuario/senha errados; 400 se faltar campo
    public LoginResponse login(@Valid @RequestBody LoginRequest dados) {
        return service.login(dados);
    }

    @PostMapping("/cadastro") // 201 com token (já entra logado); 400 por campo; 409 se o usuário existe
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse cadastrar(@Valid @RequestBody CadastroRequest dados) {
        return service.cadastrar(dados);
    }

    @RequerLogin
    @GetMapping("/me") // usado pelo front para validar a sessao salva ao recarregar a pagina
    public UsuarioResponse me(@RequestAttribute(AuthInterceptor.ATRIBUTO_USUARIO) Long usuarioId) {
        return service.usuarioAtual(usuarioId);
    }

    @RequerLogin
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestAttribute(AuthInterceptor.ATRIBUTO_TOKEN) String token) {
        service.logout(token);
    }
}
