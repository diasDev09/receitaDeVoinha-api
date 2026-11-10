package com.receitadevoinha.api.service;

import com.receitadevoinha.api.dto.ReceitaResponse;
import com.receitadevoinha.api.exception.RecursoNaoEncontradoException;
import com.receitadevoinha.api.model.Receita;
import com.receitadevoinha.api.repository.ReceitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
public class ReceitaService {

    private final ReceitaRepository repo;

    public ReceitaService(ReceitaRepository repo) {
        this.repo = repo;
    }

    /**
     * Mesma regra de filterRecipes da AV1: busca sem acento e sem caixa em
     * nome, descricao e ingredientes; categoria por igualdade.
     */
    @Transactional(readOnly = true)
    public List<ReceitaResponse> listar(String busca, String categoria) {
        String termo = normalizar(busca == null ? "" : busca.trim());
        String categoriaFiltro = categoria == null ? "" : categoria.trim();

        return repo.findAll().stream()
                .filter(r -> categoriaFiltro.isEmpty()
                        || r.getCategoria().getNome().equals(categoriaFiltro))
                .filter(r -> termo.isEmpty() || textoPesquisavel(r).contains(termo))
                .map(ReceitaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReceitaResponse buscarPorId(Long id) {
        return repo.findById(id)
                .map(ReceitaResponse::from)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Receita " + id + " não encontrada."));
    }

    private static String textoPesquisavel(Receita r) {
        return normalizar(String.join(" ",
                Stream.concat(
                        Stream.of(r.getTitulo(), r.getDescricao()),
                        r.getIngredientes().stream()).toList()));
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
