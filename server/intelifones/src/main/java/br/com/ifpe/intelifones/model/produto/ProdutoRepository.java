package br.com.ifpe.intelifones.model.produto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrue();

    List<Produto> findByVendedorId(Long vendedorId);

    List<Produto> findByVendedorIdAndAtivoTrue(Long vendedorId);

    List<Produto> findByQuantidadeGreaterThanAndAtivoTrue(Integer quantidade);

    List<Produto> findByCategoriaId(Long categoriaId);

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByPrecoBetween(Double min, Double max);

    List<Produto> findByQuantidadeGreaterThan(Integer quantidade);

    List<Produto> findByCategoriaIdAndPrecoLessThanEqual(Long categoriaId, Double precoMax);

    boolean existsByVendedorIdAndNomeIgnoreCase(Long vendedorId, String nome);

    @Modifying
    @Query("UPDATE Produto p SET p.quantidade = p.quantidade - :quantidade WHERE p.id = :id AND p.quantidade >= :quantidade")
    int diminuirQuantidade(@Param("id") Long id, @Param("quantidade") Integer quantidade);

    @Modifying
    @Query("UPDATE Produto p SET p.quantidade = p.quantidade + :quantidade WHERE p.id = :id")
    int aumentarQuantidade(@Param("id") Long id, @Param("quantidade") Integer quantidade);
}