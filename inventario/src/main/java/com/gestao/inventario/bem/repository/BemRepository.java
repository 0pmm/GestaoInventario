package com.gestao.inventario.bem.repository;

import com.gestao.inventario.bem.enums.StatusBem;
import com.gestao.inventario.bem.model.Bem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BemRepository extends JpaRepository<Bem, Long> {

    @Query("""
            select b from Bem b
            where (lower(b.nome) like lower(concat('%', :busca, '%'))
                   or lower(b.codigoPatrimonio) like lower(concat('%', :busca, '%')))
              and (:categoriaId is null
                   or exists (select c.id from b.categorias c where c.id = :categoriaId))
              and (:localizacaoId is null or b.localizacao.id = :localizacaoId)
              and ((:status is null and b.status <> :inativo) or b.status = :status)
              and (:aquisicaoDe is null or b.dataAquisicao >= :aquisicaoDe)
              and (:aquisicaoAte is null or b.dataAquisicao <= :aquisicaoAte)
            order by b.nome
            """)
    List<Bem> buscar(@Param("busca") String busca,
            @Param("categoriaId") Long categoriaId,
            @Param("localizacaoId") Long localizacaoId,
            @Param("status") StatusBem status,
            @Param("inativo") StatusBem inativo,
            @Param("aquisicaoDe") LocalDate aquisicaoDe,
            @Param("aquisicaoAte") LocalDate aquisicaoAte);

    boolean existsByCodigoPatrimonioIgnoreCase(String codigoPatrimonio);

    boolean existsByCodigoPatrimonioIgnoreCaseAndIdNot(String codigoPatrimonio, Long id);
    boolean existsByCategoriasId(Long categoriaId);

    boolean existsByLocalizacaoId(Long localizacaoId);
}