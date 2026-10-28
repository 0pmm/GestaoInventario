package com.gestao.inventario.categoria.controller;

import com.gestao.inventario.categoria.dto.CategoriaDto;
import com.gestao.inventario.categoria.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Gerenciamento das categorias de bens")
public class CategoriaController {

    private final CategoriaService service;

    @GetMapping
    @Operation(summary = "Lista as categorias (opcionalmente filtrando pelo nome)")
    public List<CategoriaDto> listar(@RequestParam(required = false) String nome) {
        return service.listar(nome);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma categoria pelo id")
    public CategoriaDto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova categoria")
    public ResponseEntity<CategoriaDto> criar(@Valid @RequestBody CategoriaDto dto) {
        CategoriaDto criada = service.criar(dto);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria")
    public CategoriaDto atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDto dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma categoria")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}