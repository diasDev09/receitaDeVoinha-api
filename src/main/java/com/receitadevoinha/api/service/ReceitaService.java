package com.receitadevoinha.api.service;

import com.receitadevoinha.api.dto.ReceitaRequest;
import com.receitadevoinha.api.dto.ReceitaResponse;
import com.receitadevoinha.api.exception.AcessoNegadoException;
import com.receitadevoinha.api.exception.CampoInvalidoException;
import com.receitadevoinha.api.exception.RecursoNaoEncontradoException;
import com.receitadevoinha.api.model.Categoria;
import com.receitadevoinha.api.model.Receita;
import com.receitadevoinha.api.model.Usuario;
import com.receitadevoinha.api.repository.CategoriaRepository;
import com.receitadevoinha.api.repository.ReceitaRepository;
import com.receitadevoinha.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
public class ReceitaService {

    private final ReceitaRepository repo;
    private final CategoriaRepository categorias;
    private final UsuarioRepository usuarios;

    public ReceitaService(ReceitaRepository repo, CategoriaRepository categorias, UsuarioRepository usuarios) {
        this.repo = repo;
        this.categorias = categorias;
        this.usuarios = usuarios;
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
        return ReceitaResponse.from(carregar(id));
    }

    /** POST /api/receitas: a receita fica registrada no nome de quem esta logado. */
    @Transactional
    public ReceitaResponse criar(ReceitaRequest dados, Long usuarioId) {
        Categoria categoria = categorias.findByNome(dados.category().trim())
                .orElseThrow(() -> new CampoInvalidoException("category", "Categoria inexistente."));
        Usuario autor = usuarios.getReferenceById(usuarioId);

        String imagem = dados.image() == null ? "" : dados.image().trim();
        Receita receita = new Receita(
                dados.title().trim(),
                dados.description().trim(),
                imagem.isEmpty() ? null : imagem,
                dados.time().trim(),
                categoria,
                autor,
                dados.ingredients().stream().map(String::trim).toList(),
                dados.preparation().stream().map(String::trim).toList());

        return ReceitaResponse.from(repo.save(receita));
    }

    /** DELETE /api/receitas/{id}: so o autor pode excluir (senao 403). */
    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Receita receita = carregar(id);
        Usuario autor = receita.getAutor();
        if (autor == null || !autor.getId().equals(usuarioId)) {
            throw new AcessoNegadoException("Você só pode excluir receitas que você cadastrou.");
        }
        usuarios.removerFavoritoDeTodos(id);
        repo.delete(receita);
    }

    private Receita carregar(Long id) {
        return repo.findById(id)
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
