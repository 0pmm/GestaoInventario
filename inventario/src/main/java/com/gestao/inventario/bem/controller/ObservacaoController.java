package com.gestao.inventario.bem.controller;

import com.gestao.inventario.bem.dto.ObservacaoRequest;
import com.gestao.inventario.bem.dto.ObservacaoResponse;
import com.gestao.inventario.bem.service.ObservacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bens/{bemId}/observacoes")
@RequiredArgsConstructor
@Tag(name = "Observações", description = "Anotações de um bem (somente criar e listar)")
public class ObservacaoController {

    private final ObservacaoService service;

    @GetMapping
    @Operation(summary = "Lista as observações de um bem, da mais recente para a mais antiga")
    public List<ObservacaoResponse> listar(@PathVariable Long bemId) {
        return service.listar(bemId);
    }

    @PostMapping
    @Operation(summary = "Adiciona uma observação ao bem")
    public ResponseEntity<ObservacaoResponse> criar(@PathVariable Long bemId,
            @Valid @RequestBody ObservacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(bemId, request));
    }
}