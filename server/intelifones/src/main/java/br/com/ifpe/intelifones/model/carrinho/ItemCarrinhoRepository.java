package br.com.ifpe.intelifones.model.carrinho;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.intelifones.model.produto.Produto;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {

    List<ItemCarrinho> findByCarrinho(Carrinho carrinho);

    Optional<ItemCarrinho> findByCarrinhoAndProduto(
            Carrinho carrinho,
            Produto produto);

}