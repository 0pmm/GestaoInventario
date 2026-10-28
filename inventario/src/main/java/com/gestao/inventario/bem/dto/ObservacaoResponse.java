package com.gestao.inventario.bem.dto;

import com.gestao.inventario.bem.model.Observacao;

import java.time.LocalDateTime;

public record ObservacaoResponse(
        Long id,
        String texto,
        LocalDateTime dataHora,
        String autor) {
    public static ObservacaoResponse de(Observacao o) {
        return new ObservacaoResponse(
                o.getId(),
                o.getTexto(),
                o.getDataHora(),
                o.getAutor().getNome());
    }
}