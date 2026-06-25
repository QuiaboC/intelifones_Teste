package br.com.ifpe.intelifones.api.carrinho;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import br.com.ifpe.intelifones.model.carrinho.CarrinhoService;
import br.com.ifpe.intelifones.model.carrinho.ItemCarrinho;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carrinho")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "*"
})
@Tag(name = "Carrinho", description = "Endpoints do carrinho de compras")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final UsuarioService usuarioService;

    private Long getUsuarioLogadoId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Usuario usuario = usuarioService.buscarPorEmail(email);

        return usuario.getId();
    }

    @Operation(summary = "Adicionar produto ao carrinho")
    @PostMapping
    public ResponseEntity<Void> adicionar(
            @Valid @RequestBody CarrinhoRequest request) {

        carrinhoService.adicionarProduto(
                getUsuarioLogadoId(),
                request.getProdutoId(),
                request.getQuantidade());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Listar itens do carrinho")
    @GetMapping
    public List<ItemCarrinho> listar() {
        return carrinhoService.listarCarrinho(
                getUsuarioLogadoId());
    }

    @Operation(summary = "Remover item do carrinho")
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> remover(
            @PathVariable Long itemId) {

        carrinhoService.removerItem(itemId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Limpar carrinho")
    @DeleteMapping("/limpar")
    public ResponseEntity<Void> limpar() {

        carrinhoService.limparCarrinho(
                getUsuarioLogadoId());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar quantidade de um item do carrinho")
    @PutMapping("/item/{itemId}")
    public ResponseEntity<Void> atualizarQuantidade(
        @PathVariable Long itemId,
        @RequestParam Integer quantidade) {

    carrinhoService.atualizarQuantidade(itemId, quantidade);

    return ResponseEntity.noContent().build();
}
}