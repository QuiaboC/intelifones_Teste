package br.com.ifpe.intelifones.model.usuario;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.ifpe.intelifones.api.auth.RegisterRequest;
import br.com.ifpe.intelifones.api.usuarios.AtualizarPerfilRequest;
import br.com.ifpe.intelifones.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario registrar(RegisterRequest request) {

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail já cadastrado!");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .telefone(request.getTelefone())
                .endereco(request.getEndereco())
                .role(request.getRole())
                .ativo(true)
                .build();

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado: " + email));
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com ID: " + id));
    }

    @Transactional
public Usuario atualizarPerfil(Long usuarioId, AtualizarPerfilRequest request) {
    Usuario usuario = buscarPorId(usuarioId);

    if (request.getNome() != null && !request.getNome().trim().isEmpty()) {
        usuario.setNome(request.getNome());
    }

    if (request.getTelefone() != null) {
        usuario.setTelefone(request.getTelefone());
    }

    if (request.getEndereco() != null) {
        usuario.setEndereco(request.getEndereco());
    }

    return usuarioRepository.save(usuario);
}

public Usuario atualizarImagem(Long usuarioId, MultipartFile arquivo) {

    Usuario usuario = buscarPorId(usuarioId);

    try {

        String nomeArquivo =
                UUID.randomUUID() + "_" +
                arquivo.getOriginalFilename();

        Path pastaUploads =
                Paths.get("uploads", "usuarios");

        Files.createDirectories(pastaUploads);

        Path caminho =
                pastaUploads.resolve(nomeArquivo);

        Files.copy(
                arquivo.getInputStream(),
                caminho,
                StandardCopyOption.REPLACE_EXISTING
        );

        usuario.setImagem(nomeArquivo);

        return usuarioRepository.save(usuario);

    } catch (IOException e) {
        throw new RuntimeException("Erro ao salvar imagem");
    }
}
}