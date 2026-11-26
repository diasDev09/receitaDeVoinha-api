package com.receitadevoinha.api.repository;

import com.receitadevoinha.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    /** Tira a receita dos favoritos de todos antes de exclui-la (chave estrangeira). */
    @Modifying
    // {h-schema} vira "voinha." (consulta nativa não recebe o schema padrão sozinha)
    @Query(value = "DELETE FROM {h-schema}usuario_favoritos WHERE receita_id = :receitaId", nativeQuery = true)
    void removerFavoritoDeTodos(@Param("receitaId") Long receitaId);
}
