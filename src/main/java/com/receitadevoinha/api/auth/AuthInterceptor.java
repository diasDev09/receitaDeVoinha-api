package com.receitadevoinha.api.auth;

import com.receitadevoinha.api.exception.NaoAutenticadoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Protege os metodos anotados com {@link RequerLogin}. Quando o token e valido,
 * deixa o id do usuario e o token como atributos da requisicao para o controller.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_USUARIO = "usuarioId";
    public static final String ATRIBUTO_TOKEN = "token";

    private final SessaoService sessoes;

    public AuthInterceptor(SessaoService sessoes) {
        this.sessoes = sessoes;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod metodo) || !metodo.hasMethodAnnotation(RequerLogin.class)) {
            return true; // endpoint publico (ou preflight de CORS)
        }

        String token = extrairToken(request.getHeader("Authorization"));
        Long usuarioId = sessoes.usuarioDoToken(token)
                .orElseThrow(() -> new NaoAutenticadoException(
                        "Sessão ausente ou expirada. Faça login novamente."));

        request.setAttribute(ATRIBUTO_USUARIO, usuarioId);
        request.setAttribute(ATRIBUTO_TOKEN, token);
        return true;
    }

    private static String extrairToken(String cabecalho) {
        if (cabecalho == null || !cabecalho.startsWith("Bearer ")) return null;
        String token = cabecalho.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}
