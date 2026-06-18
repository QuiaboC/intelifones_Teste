package br.com.ifpe.intelifones.model.favorito;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritoRepository
        extends JpaRepository<Favorito, Long> {

    List<Favorito> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndProdutoId(
            Long usuarioId,
            Long produtoId);

    void deleteByUsuarioIdAndProdutoId(
            Long usuarioId,
            Long produtoId);
}