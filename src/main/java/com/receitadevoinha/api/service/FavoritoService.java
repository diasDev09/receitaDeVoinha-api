package com.receitadevoinha.api.service;

import com.receitadevoinha.api.dto.ReceitaResponse;
import com.receitadevoinha.api.exception.NaoAutenticadoException;
import com.receitadevoinha.api.exception.RecursoNaoEncontradoException;
import com.receitadevoinha.api.model.Receita;
import com.receitadevoinha.api.model.Usuario;
import com.receitadevoinha.api.repository.ReceitaRepository;
import com.receitadevoinha.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Favoritos de cada usuario. Favoritar duas vezes ou remover o que nao existe nao e erro. */
@Service
public class FavoritoService {

    private final UsuarioRepository usuarios;
    private final ReceitaRepository receitas;

    public FavoritoService(UsuarioRepository usuarios, ReceitaRepository receitas) {
        this.usuarios = usuarios;
        this.receitas = receitas;
    }

    @Transactional(readOnly = true)
    public List<ReceitaResponse> listar(Long usuarioId) {
        return usuario(usuarioId).getFavoritos().stream().map(ReceitaResponse::from).toList();
    }

    @Transactional
    public void adicionar(Long usuarioId, Long receitaId) {
        Receita receita = receitas.findById(receitaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Receita " + receitaId + " não encontrada."));
        usuario(usuarioId).getFavoritos().add(receita);
    }

    @Transactional
    public void remover(Long usuarioId, Long receitaId) {
        usuario(usuarioId).getFavoritos().removeIf(r -> r.getId().equals(receitaId));
    }

    private Usuario usuario(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new NaoAutenticadoException("Usuário da sessão não existe mais."));
    }
}
