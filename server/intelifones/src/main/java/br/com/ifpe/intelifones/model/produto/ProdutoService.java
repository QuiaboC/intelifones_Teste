package br.com.ifpe.intelifones.model.produto;

import br.com.ifpe.intelifones.model.categoria.Categoria;
import br.com.ifpe.intelifones.model.categoria.CategoriaRepository;
import br.com.ifpe.intelifones.model.usuario.Role;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioRepository;
import br.com.ifpe.intelifones.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Produto save(Produto produto, Long vendedorId) {

        // 1. Validar vendedor
        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new BusinessException("Vendedor não encontrado"));

        if (vendedor.getRole() != Role.VENDEDOR) {
            throw new BusinessException("Apenas usuários com role VENDEDOR podem criar produtos");
        }

        // 2. Validar categoria
        if (produto.getCategoria() == null || produto.getCategoria().getId() == null) {
            throw new BusinessException("Categoria é obrigatória");
        }

        Categoria categoria = categoriaRepository.findById(produto.getCategoria().getId())
                .orElseThrow(() -> new BusinessException("Categoria não encontrada"));

        // 3. Validar dados do produto
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome do produto é obrigatório");
        }

        if (produto.getPreco() == null || produto.getPreco() <= 0) {
            throw new BusinessException("Preço deve ser maior que zero");
        }

        if (produto.getQuantidade() == null || produto.getQuantidade() < 0) {
            throw new BusinessException("Quantidade inválida");
        }

        // 4. Verificar se vendedor já tem produto com mesmo nome
        if (produtoRepository.existsByVendedorIdAndNomeIgnoreCase(vendedorId, produto.getNome())) {
            throw new BusinessException("Você já possui um produto com este nome");
        }

        produto.setCategoria(categoria);
        produto.setVendedor(vendedor);
        produto.setAtivo(true);

        return produtoRepository.save(produto);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findByAtivoTrue();
    }

    public List<Produto> listarDisponiveis() {
        return produtoRepository.findByQuantidadeGreaterThan(0);
    }

    public List<Produto> listarPorVendedor(Long vendedorId) {
        return produtoRepository.findByVendedorId(vendedorId);
    }

    public List<Produto> listarPorCategoria(Long categoriaId) {
        return produtoRepository.findByCategoriaId(categoriaId);
    }

    public List<Produto> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return listarTodos();
        }
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Produto obterPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado com ID: " + id));
    }

    @Transactional
    public Produto update(Long id, Produto produtoAlterado, Long vendedorId) {

        Produto produto = obterPorId(id);

        // Verificar se o produto pertence ao vendedor
        if (!produto.getVendedor().getId().equals(vendedorId)) {
            throw new BusinessException("Você só pode editar seus próprios produtos");
        }

        if (produtoAlterado.getNome() != null && !produtoAlterado.getNome().trim().isEmpty()) {
            produto.setNome(produtoAlterado.getNome());
        }

        if (produtoAlterado.getDescricao() != null) {
            produto.setDescricao(produtoAlterado.getDescricao());
        }

        if (produtoAlterado.getPreco() != null && produtoAlterado.getPreco() > 0) {
            produto.setPreco(produtoAlterado.getPreco());
        }

        if (produtoAlterado.getQuantidade() != null && produtoAlterado.getQuantidade() >= 0) {
            produto.setQuantidade(produtoAlterado.getQuantidade());
        }

        if (produtoAlterado.getUsado() != null) {
            produto.setUsado(produtoAlterado.getUsado());
        }

        if (produtoAlterado.getCategoria() != null && produtoAlterado.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(produtoAlterado.getCategoria().getId())
                    .orElseThrow(() -> new BusinessException("Categoria não encontrada"));
            produto.setCategoria(categoria);
        }

        return produtoRepository.save(produto);
    }

    @Transactional
    public void delete(Long id, Long vendedorId) {
        Produto produto = obterPorId(id);

        if (!produto.getVendedor().getId().equals(vendedorId)) {
            throw new BusinessException("Você só pode deletar seus próprios produtos");
        }

        produto.setAtivo(false); // Soft delete
        produtoRepository.save(produto);
    }

    @Transactional
    public void comprar(Long id, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new BusinessException("Quantidade deve ser maior que zero");
        }

        int updated = produtoRepository.diminuirQuantidade(id, quantidade);

        if (updated == 0) {
            throw new BusinessException("Estoque insuficiente para esta compra");
        }
    }

    @Transactional
    public void reporEstoque(Long id, Integer quantidade, Long vendedorId) {
        Produto produto = obterPorId(id);

        if (!produto.getVendedor().getId().equals(vendedorId)) {
            throw new BusinessException("Você só pode repor estoque dos seus próprios produtos");
        }

        if (quantidade == null || quantidade <= 0) {
            throw new BusinessException("Quantidade deve ser maior que zero");
        }

        produtoRepository.aumentarQuantidade(id, quantidade);
    }

    public Produto uploadImagem(
        Long produtoId,
        MultipartFile arquivo,
        Long vendedorId) {

    Produto produto = obterPorId(produtoId);

    if (!produto.getVendedor().getId().equals(vendedorId)) {
        throw new IllegalArgumentException(
                "Você não pode alterar este produto");
    }

    try {

        String nomeArquivo =
                UUID.randomUUID() + "_" +
                arquivo.getOriginalFilename();

        Path pastaUploads =
                Paths.get("uploads");

        Files.createDirectories(pastaUploads);

        Path caminho =
                pastaUploads.resolve(nomeArquivo);

        Files.copy(
                arquivo.getInputStream(),
                caminho,
                StandardCopyOption.REPLACE_EXISTING
        );

        produto.setImagem(nomeArquivo);

        return produtoRepository.save(produto);

    } catch (IOException e) {
        throw new RuntimeException(
                "Erro ao salvar imagem");
    }
}
}