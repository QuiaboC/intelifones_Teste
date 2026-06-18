package br.com.ifpe.intelifones.api.pedido;

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

@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:4000",
        "http://localhost:8081"
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
    public ResponseEntity<Pedido> finalizarCompra() {

        Long usuarioId = getUsuarioLogadoId();

        Pedido pedido = pedidoService.finalizarCompra(usuarioId);

        return new ResponseEntity<>(pedido, HttpStatus.CREATED);
    }
}