package com.gestao.inventario.bem.repository;

import com.gestao.inventario.bem.model.Observacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservacaoRepository extends JpaRepository<Observacao, Long> {

    List<Observacao> findByBemIdOrderByDataHoraDescIdDesc(Long bemId);

    boolean existsByBemId(Long bemId);

    boolean existsByAutorId(Long autorId);
}