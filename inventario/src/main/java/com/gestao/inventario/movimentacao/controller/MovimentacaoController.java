package com.gestao.inventario.movimentacao.controller;

import com.gestao.inventario.movimentacao.dto.MovimentacaoResponse;
import com.gestao.inventario.movimentacao.enums.TipoMovimentacao;
import com.gestao.inventario.movimentacao.service.MovimentacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Histórico", description = "Consulta do histórico de movimentações (somente leitura)")
public class MovimentacaoController {

    private final MovimentacaoService service;

    @GetMapping("/movimentacoes")
    @Operation(summary = "Histórico geral (administrador vê tudo; usuário comum, só as mudanças dos bens)")
    public List<MovimentacaoResponse> listar(
            @RequestParam(required = false) Long bemId,
            @RequestParam(required = false) TipoMovimentacao tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
            @RequestParam(required = false) Long responsavelId) {
        return service.listar(bemId, tipo, de, ate, responsavelId);
    }

    @GetMapping("/bens/{bemId}/movimentacoes")
    @Operation(summary = "Histórico já filtrado para um bem (botão 'Histórico' do bem)")
    public List<MovimentacaoResponse> listarDoBem(
            @PathVariable Long bemId,
            @RequestParam(required = false) TipoMovimentacao tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {
        return service.listar(bemId, tipo, de, ate, null);
    }

    @GetMapping("/movimentacoes/{id}")
    @Operation(summary = "Detalhes de uma movimentação")
    public MovimentacaoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}