package br.com.ifpe.intelifones.model.carrinho;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifpe.intelifones.model.produto.Produto;
import br.com.ifpe.intelifones.model.produto.ProdutoRepository;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioRepository;
import br.com.ifpe.intelifones.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public void adicionarProduto(
            Long usuarioId,
            Long produtoId,
            Integer quantidade) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new BusinessException("Usuário não encontrado"));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new BusinessException("Produto não encontrado"));

        if (!produto.getAtivo()) {
            throw new BusinessException("Produto indisponível");
        }

        if (produto.getQuantidade() < quantidade) {
            throw new BusinessException("Estoque insuficiente");
        }

        Carrinho carrinho = carrinhoRepository
                .findByUsuario(usuario)
                .orElseGet(() -> carrinhoRepository.save(
                        Carrinho.builder()
                                .usuario(usuario)
                                .build()));

        ItemCarrinho item = itemCarrinhoRepository
                .findByCarrinhoAndProduto(carrinho, produto)
                .orElse(null);

        if (item != null) {
            item.setQuantidade(item.getQuantidade() + quantidade);
        } else {
            item = ItemCarrinho.builder()
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(quantidade)
                    .build();
        }

        itemCarrinhoRepository.save(item);
    }

    public List<ItemCarrinho> listarCarrinho(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new BusinessException("Usuário não encontrado"));

        Carrinho carrinho = carrinhoRepository
                .findByUsuario(usuario)
                .orElseThrow(() ->
                        new BusinessException("Carrinho não encontrado"));

        return itemCarrinhoRepository.findByCarrinho(carrinho);
    }

    public void removerItem(Long itemId) {

        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() ->
                        new BusinessException("Item não encontrado"));

        itemCarrinhoRepository.delete(item);
    }

    public void limparCarrinho(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new BusinessException("Usuário não encontrado"));

        Carrinho carrinho = carrinhoRepository
                .findByUsuario(usuario)
                .orElseThrow(() ->
                        new BusinessException("Carrinho não encontrado"));

        List<ItemCarrinho> itens =
                itemCarrinhoRepository.findByCarrinho(carrinho);

        itemCarrinhoRepository.deleteAll(itens);
    }
}