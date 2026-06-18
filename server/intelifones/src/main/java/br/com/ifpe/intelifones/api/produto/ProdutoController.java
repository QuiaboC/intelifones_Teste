package br.com.ifpe.intelifones.api.produto;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.ifpe.intelifones.model.produto.Produto;
import br.com.ifpe.intelifones.model.produto.ProdutoService;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4000", "http://localhost:8081"})
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    private Long getVendedorIdLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return usuario.getId();
    }

    @Operation(summary = "Criar um novo produto (anunciar)")
    @PostMapping
    public ResponseEntity<Produto> create(@Valid @RequestBody ProdutoRequest request) {
        Produto produto = request.build();
        Long vendedorId = getVendedorIdLogado();
        Produto saved = produtoService.save(produto, vendedorId);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos os produtos ativos")
    @GetMapping
    public List<Produto> listAll() {
        return produtoService.listarTodos();
    }

    @Operation(summary = "Listar produtos com estoque disponível")
    @GetMapping("/disponiveis")
    public List<Produto> listDisponiveis() {
        return produtoService.listarDisponiveis();
    }

    @Operation(summary = "Buscar produto por ID")
    @GetMapping("/{id}")
    public Produto getById(@PathVariable Long id) {
        return produtoService.obterPorId(id);
    }

    @Operation(summary = "Listar meus produtos (vendedor logado)")
    @GetMapping("/meus")
    public List<Produto> listMeusProdutos() {
        Long vendedorId = getVendedorIdLogado();
        return produtoService.listarPorVendedor(vendedorId);
    }

    @Operation(summary = "Listar produtos por categoria")
    @GetMapping("/categoria/{categoriaId}")
    public List<Produto> listByCategoria(@PathVariable Long categoriaId) {
        return produtoService.listarPorCategoria(categoriaId);
    }

    @Operation(summary = "Buscar produtos por nome")
    @GetMapping("/buscar")
    public List<Produto> searchByNome(@RequestParam String nome) {
        return produtoService.buscarPorNome(nome);
    }

    @Operation(summary = "Atualizar um produto")
    @PutMapping("/{id}")
    public ResponseEntity<Produto> update(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        Produto produto = request.build();
        Long vendedorId = getVendedorIdLogado();
        Produto updated = produtoService.update(id, produto, vendedorId);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Remover um produto (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long vendedorId = getVendedorIdLogado();
        produtoService.delete(id, vendedorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Comprar um produto (diminui estoque)")
    @PatchMapping("/{id}/comprar")
    public ResponseEntity<Void> comprar(@PathVariable Long id, @RequestParam Integer quantidade) {
        produtoService.comprar(id, quantidade);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Repor estoque de um produto")
    @PatchMapping("/{id}/repor")
    public ResponseEntity<Void> reporEstoque(@PathVariable Long id, @RequestParam Integer quantidade) {
        Long vendedorId = getVendedorIdLogado();
        produtoService.reporEstoque(id, quantidade, vendedorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload da imagem do produto")
    @PostMapping("/{id}/imagem")
    public ResponseEntity<Produto> uploadImagem(
        @PathVariable Long id,
        @RequestParam("arquivo") MultipartFile arquivo) {

        Long vendedorId = getVendedorIdLogado();

        Produto produto =
            produtoService.uploadImagem(id, arquivo, vendedorId);

    return ResponseEntity.ok(produto);
}
}