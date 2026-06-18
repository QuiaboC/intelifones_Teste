package br.com.ifpe.intelifones.model.pedido;

import br.com.ifpe.intelifones.util.exception.BusinessException;
import br.com.ifpe.intelifones.model.carrinho.Carrinho;
import br.com.ifpe.intelifones.model.carrinho.CarrinhoRepository;
import br.com.ifpe.intelifones.model.carrinho.ItemCarrinho;
import br.com.ifpe.intelifones.model.carrinho.ItemCarrinhoRepository;
import br.com.ifpe.intelifones.model.produto.Produto;
import br.com.ifpe.intelifones.model.produto.ProdutoRepository;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;

    @Transactional
    public Pedido finalizarCompra(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new BusinessException("Usuário não encontrado"));

        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() ->
                        new BusinessException("Carrinho não encontrado"));

        List<ItemCarrinho> itensCarrinho =
                itemCarrinhoRepository.findByCarrinho(carrinho);

        if (itensCarrinho.isEmpty()) {
            throw new BusinessException("Carrinho vazio");
        }

        double valorTotal = 0.0;

        Pedido pedido = Pedido.builder()
                .comprador(usuario)
                .status(StatusPedido.PENDENTE)
                .dataPedido(LocalDateTime.now())
                .valorTotal(0.0)
                .build();

        pedido = pedidoRepository.save(pedido);

        for (ItemCarrinho item : itensCarrinho) {

            Produto produto = item.getProduto();

            if (!produto.getAtivo()) {
                throw new BusinessException(
                        "Produto indisponível: " + produto.getNome());
            }

            if (produto.getQuantidade() < item.getQuantidade()) {
                throw new BusinessException(
                        "Estoque insuficiente para: " + produto.getNome());
            }

            ItemPedido itemPedido = ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(item.getQuantidade())
                    .precoUnitario(produto.getPreco())
                    .build();

            itemPedidoRepository.save(itemPedido);

            valorTotal +=
                    produto.getPreco() * item.getQuantidade();

            produto.setQuantidade(
                    produto.getQuantidade() - item.getQuantidade());

            produtoRepository.save(produto);
        }

        pedido.setValorTotal(valorTotal);
        pedidoRepository.save(pedido);

        itemCarrinhoRepository.deleteAll(itensCarrinho);

        return pedido;
    }
}