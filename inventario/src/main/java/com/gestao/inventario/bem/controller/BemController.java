package com.gestao.inventario.bem.controller;

import com.gestao.inventario.bem.dto.BemRequest;
import com.gestao.inventario.bem.dto.BemResponse;
import com.gestao.inventario.bem.enums.StatusBem;
import com.gestao.inventario.bem.service.BemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bens")
@RequiredArgsConstructor
@Tag(name = "Bens", description = "Gerenciamento e consulta dos bens do inventário")
public class BemController {

    private final BemService service;

    @GetMapping
    @Operation(summary = "Lista bens com busca e filtros (inativos só aparecem se pedidos)")
    public List<BemResponse> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long localizacaoId,
            @RequestParam(required = false) StatusBem status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate aquisicaoDe,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate aquisicaoAte) {
        return service.listar(busca, categoriaId, localizacaoId, status, aquisicaoDe, aquisicaoAte);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um bem pelo id")
    public BemResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo bem")
    public ResponseEntity<BemResponse> criar(@Valid @RequestBody BemRequest request) {
        BemResponse criado = service.criar(request);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um bem")
    public BemResponse atualizar(@PathVariable Long id, @Valid @RequestBody BemRequest request) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativa um bem")
    public BemResponse inativar(@PathVariable Long id, @RequestParam(required = false) String motivo) {
        return service.inativar(id, motivo);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um bem (bens com histórico só podem ser inativados)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}