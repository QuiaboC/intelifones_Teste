package br.com.ifpe.intelifones.api.pedido;

import br.com.ifpe.intelifones.model.pedido.ItemPedido;
import br.com.ifpe.intelifones.model.pedido.Pedido;
import br.com.ifpe.intelifones.model.pedido.PedidoService;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "*"
})
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    private Long getUsuarioLogadoId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Usuario usuario = usuarioService.buscarPorEmail(email);

        return usuario.getId();
    }

    @Operation(summary = "Finalizar compra do carrinho")
    @PostMapping("/finalizar")
    public ResponseEntity<Pedido> finalizarCompra(@RequestBody FinalizarCompraRequest request) {
        Long usuarioId = getUsuarioLogadoId();
        Pedido pedido = pedidoService.finalizarCompra(usuarioId, request);
        return new ResponseEntity<>(pedido, HttpStatus.CREATED);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<ItemPedido>> historico() {

    return ResponseEntity.ok(
            pedidoService.listarHistoricoCompras(
                    getUsuarioLogadoId()));
    }

    @Operation(summary = "Listar vendas do vendedor logado")
@GetMapping("/vendas")
public ResponseEntity<List<ItemPedido>> minhasVendas() {
    return ResponseEntity.ok(
        pedidoService.listarVendasDoVendedor(getUsuarioLogadoId())
    );
}
}