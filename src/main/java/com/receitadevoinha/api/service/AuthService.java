package com.receitadevoinha.api.service;

import com.receitadevoinha.api.auth.SessaoService;
import com.receitadevoinha.api.dto.LoginRequest;
import com.receitadevoinha.api.dto.LoginResponse;
import com.receitadevoinha.api.dto.UsuarioResponse;
import com.receitadevoinha.api.exception.NaoAutenticadoException;
import com.receitadevoinha.api.model.Usuario;
import com.receitadevoinha.api.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final SessaoService sessoes;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, SessaoService sessoes) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.sessoes = sessoes;
    }

    /** Mesma mensagem para usuario inexistente e senha errada (nao revela qual falhou). */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest dados) {
        String login = dados.username().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarios.findByLogin(login)
                .filter(u -> encoder.matches(dados.password(), u.getSenhaHash()))
                .orElseThrow(() -> new NaoAutenticadoException("Usuário ou senha inválidos."));

        return new LoginResponse(sessoes.criar(usuario.getId()), UsuarioResponse.from(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse usuarioAtual(Long usuarioId) {
        return usuarios.findById(usuarioId)
                .map(UsuarioResponse::from)
                .orElseThrow(() -> new NaoAutenticadoException("Usuário da sessão não existe mais."));
    }

    public void logout(String token) {
        sessoes.encerrar(token);
    }
}
