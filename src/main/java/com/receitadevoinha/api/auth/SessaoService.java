package com.receitadevoinha.api.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sessoes com token opaco guardadas em memoria. O token e aleatorio (256 bits),
 * entao nao ha chave secreta para versionar. Ao reiniciar a API todas as sessoes
 * caem e o front recebe 401, que trata como "sessao expirada".
 */
@Service
public class SessaoService {

    private record Sessao(Long usuarioId, Instant expiraEm) {
    }

    private final SecureRandom random = new SecureRandom();
    private final Map<String, Sessao> sessoes = new ConcurrentHashMap<>();
    private final Duration validade;

    public SessaoService(@Value("${app.sessao.horas:8}") long horas) {
        this.validade = Duration.ofHours(horas);
    }

    public String criar(Long usuarioId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessoes.put(token, new Sessao(usuarioId, Instant.now().plus(validade)));
        return token;
    }

    /** Id do usuario dono do token, se o token existir e nao estiver vencido. */
    public Optional<Long> usuarioDoToken(String token) {
        if (token == null) return Optional.empty();
        Sessao sessao = sessoes.get(token);
        if (sessao == null) return Optional.empty();
        if (sessao.expiraEm().isBefore(Instant.now())) {
            sessoes.remove(token);
            return Optional.empty();
        }
        return Optional.of(sessao.usuarioId());
    }

    public void encerrar(String token) {
        if (token != null) sessoes.remove(token);
    }
}
