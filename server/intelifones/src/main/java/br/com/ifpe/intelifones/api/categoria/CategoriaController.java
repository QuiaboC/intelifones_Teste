package br.com.ifpe.intelifones.api.categoria;

import br.com.ifpe.intelifones.model.categoria.Categoria;
import br.com.ifpe.intelifones.model.categoria.CategoriaRepository;
import br.com.ifpe.intelifones.util.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias de produtos")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    @Operation(summary = "Criar uma nova categoria")
    @PostMapping
    public ResponseEntity<Categoria> create(@Valid @RequestBody CategoriaRequest request) {
        
        if (categoriaRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new BusinessException("Já existe uma categoria com este nome");
        }
        
        Categoria categoria = request.build();
        Categoria saved = categoriaRepository.save(categoria);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todas as categorias")
    @GetMapping
    public List<Categoria> listAll() {
        return categoriaRepository.findAllByOrderByNomeAsc();
    }

    @Operation(summary = "Buscar categoria por ID")
    @GetMapping("/{id}")
    public Categoria getById(@PathVariable Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com ID: " + id));
    }
}