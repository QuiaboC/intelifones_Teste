package br.com.ifpe.intelifones.model.favorito;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface FavoritoRepository
        extends JpaRepository<Favorito, Long> {

    List<Favorito> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndProdutoId(
            Long usuarioId,
            Long produtoId);

        @Modifying
    void deleteByUsuarioIdAndProdutoId(
            Long usuarioId,
            Long produtoId);
}