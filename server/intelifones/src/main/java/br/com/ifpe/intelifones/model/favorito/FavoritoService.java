package br.com.ifpe.intelifones.model.favorito;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ifpe.intelifones.model.produto.Produto;
import br.com.ifpe.intelifones.model.produto.ProdutoRepository;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioRepository;
import br.com.ifpe.intelifones.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public void adicionar(Long usuarioId, Long produtoId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new BusinessException("Usuário não encontrado"));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new BusinessException("Produto não encontrado"));

        boolean existe = favoritoRepository
                .existsByUsuarioIdAndProdutoId(usuarioId, produtoId);

        if (existe) return;

        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .produto(produto)
                .build();

        favoritoRepository.save(favorito);
    }

    public void remover(Long usuarioId, Long produtoId) {

        favoritoRepository.deleteByUsuarioIdAndProdutoId(
                usuarioId,
                produtoId);
    }

    public List<Produto> listar(Long usuarioId) {

        return favoritoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(Favorito::getProduto)
                .toList();
    }
}