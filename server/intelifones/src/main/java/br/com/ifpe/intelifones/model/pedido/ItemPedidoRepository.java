package br.com.ifpe.intelifones.model.pedido;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    List<ItemPedido> findByPedido(Pedido pedido);

    List<ItemPedido> findByProduto_Vendedor_Id(Long vendedorId);

}