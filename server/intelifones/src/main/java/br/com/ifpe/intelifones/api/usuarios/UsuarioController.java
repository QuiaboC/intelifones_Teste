package br.com.ifpe.intelifones.api.usuarios;

import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints de perfil do usuário")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private Long getUsuarioLogadoId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return usuario.getId();
    }

    @Operation(summary = "Buscar dados do usuário logado")
    @GetMapping("/me")
    public Usuario meusDados() {
        return usuarioService.buscarPorId(getUsuarioLogadoId());
    }

    @Operation(summary = "Atualizar dados do perfil (nome, telefone, endereço)")
    @PutMapping("/me")
    public Usuario atualizarPerfil(@RequestBody AtualizarPerfilRequest request) {
        return usuarioService.atualizarPerfil(getUsuarioLogadoId(), request);
    }

    @Operation(summary = "Atualizar foto de perfil")
    @PutMapping(value = "/me/imagem", consumes = "multipart/form-data")
    public Usuario atualizarImagem(@RequestParam("arquivo") MultipartFile arquivo) {
        return usuarioService.atualizarImagem(getUsuarioLogadoId(), arquivo);
    }
}