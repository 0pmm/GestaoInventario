package com.gestao.inventario.movimentacao.repository;

import com.gestao.inventario.movimentacao.enums.TipoMovimentacao;
import com.gestao.inventario.movimentacao.model.Movimentacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    @Query("""
            select m from Movimentacao m
            where m.tipo in :tipos
              and (:bemId is null or m.bem.id = :bemId)
              and (:tipo is null or m.tipo = :tipo)
              and (:inicio is null or m.dataHora >= :inicio)
              and (:fim is null or m.dataHora < :fim)
              and (:responsavelId is null or m.responsavel.id = :responsavelId)
            order by m.dataHora desc, m.id desc
            """)
    List<Movimentacao> buscar(@Param("tipos") Collection<TipoMovimentacao> tipos,
            @Param("bemId") Long bemId,
            @Param("tipo") TipoMovimentacao tipo,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("responsavelId") Long responsavelId);

    boolean existsByBemId(Long bemId);

    boolean existsByOrigemIdOrDestinoId(Long origemId, Long destinoId);

    boolean existsByResponsavelId(Long responsavelId);
}