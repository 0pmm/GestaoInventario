package com.gestao.inventario.movimentacao.dto;

import com.gestao.inventario.movimentacao.enums.TipoMovimentacao;
import com.gestao.inventario.movimentacao.model.Movimentacao;

import java.time.LocalDateTime;

public record MovimentacaoResponse(
        Long id,
        BemRef bem,
        TipoMovimentacao tipo,
        LocalDateTime dataHora,
        String valorAnterior,
        String valorNovo,
        String origem,
        String destino,
        String motivo,
        String responsavel
) {
    public record BemRef(Long id, String codigoPatrimonio, String nome) { }

    // admin = true: mostra o responsável
    // admin = false: o responsável fica oculto
    public static MovimentacaoResponse de(Movimentacao m, boolean admin) {
        String responsavel = null;
        if (admin) {
            responsavel = (m.getResponsavel() != null) ? m.getResponsavel().getNome() : "Sistema";
        }

        return new MovimentacaoResponse(
                m.getId(),
                new BemRef(m.getBem().getId(), m.getBem().getCodigoPatrimonio(), m.getBem().getNome()),
                m.getTipo(),
                m.getDataHora(),
                m.getValorAnterior(),
                m.getValorNovo(),
                m.getOrigem() != null ? m.getOrigem().getNome() : null,
                m.getDestino() != null ? m.getDestino().getNome() : null,
                m.getMotivo(),
                responsavel);
    }
}