package br.com.ifpe.intelifones.api.favorito;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import br.com.ifpe.intelifones.model.favorito.FavoritoService;
import br.com.ifpe.intelifones.model.produto.Produto;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
@Tag(name = "Favoritos", description = "Endpoints de produtos favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final UsuarioService usuarioService;

    private Long getUsuarioLogadoId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Usuario usuario = usuarioService.buscarPorEmail(email);

        return usuario.getId();
    }

    @Operation(summary = "Adicionar produto aos favoritos")
    @PostMapping("/{produtoId}")
    public ResponseEntity<Void> adicionar(@PathVariable Long produtoId) {

        favoritoService.adicionar(
                getUsuarioLogadoId(),
                produtoId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remover produto dos favoritos")
    @DeleteMapping("/{produtoId}")
    public ResponseEntity<Void> remover(@PathVariable Long produtoId) {

        favoritoService.remover(
                getUsuarioLogadoId(),
                produtoId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar favoritos do usuário")
    @GetMapping
    public List<Produto> listar() {

        return favoritoService.listar(
                getUsuarioLogadoId());
    }
}