package com.gestao.inventario.bem.dto;

import com.gestao.inventario.bem.enums.EstadoConservacao;
import com.gestao.inventario.bem.enums.StatusBem;
import com.gestao.inventario.bem.model.Bem;
import com.gestao.inventario.categoria.model.Categoria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public record BemResponse(
        Long id,
        String codigoPatrimonio,
        String nome,
        String descricao,
        Integer quantidade,
        BigDecimal valorAquisicao,
        LocalDate dataAquisicao,
        EstadoConservacao estadoConservacao,
        StatusBem status,
        Referencia localizacao,
        List<Referencia> categorias) {

    public record Referencia(Long id, String nome) {
    }

    public static BemResponse de(Bem bem) {
        List<Referencia> categorias = bem.getCategorias().stream()
                .sorted(Comparator.comparing(Categoria::getNome))
                .map(c -> new Referencia(c.getId(), c.getNome()))
                .toList();

        return new BemResponse(
                bem.getId(),
                bem.getCodigoPatrimonio(),
                bem.getNome(),
                bem.getDescricao(),
                bem.getQuantidade(),
                bem.getValorAquisicao(),
                bem.getDataAquisicao(),
                bem.getEstadoConservacao(),
                bem.getStatus(),
                new Referencia(bem.getLocalizacao().getId(), bem.getLocalizacao().getNome()),
                categorias);
    }
}