package com.receitadevoinha.api.service;

import com.receitadevoinha.api.auth.SessaoService;
import com.receitadevoinha.api.dto.CadastroRequest;
import com.receitadevoinha.api.dto.LoginRequest;
import com.receitadevoinha.api.dto.LoginResponse;
import com.receitadevoinha.api.dto.UsuarioResponse;
import com.receitadevoinha.api.exception.ConflitoException;
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

    /**
     * POST /api/auth/cadastro: cria a conta (senha só como hash BCrypt) e já
     * devolve uma sessão, para a pessoa entrar logada depois de se cadastrar.
     * O login é guardado em minúsculas, como o login compara.
     */
    @Transactional
    public LoginResponse cadastrar(CadastroRequest dados) {
        String login = dados.username().trim().toLowerCase(Locale.ROOT);
        if (usuarios.existsByLogin(login)) {
            throw new ConflitoException("username", "Esse nome de usuário já está em uso.");
        }

        Usuario usuario = usuarios.save(new Usuario(
                dados.name().trim(), login, encoder.encode(dados.password())));

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
