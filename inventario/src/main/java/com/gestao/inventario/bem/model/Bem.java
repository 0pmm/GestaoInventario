package com.gestao.inventario.bem.model;

import com.gestao.inventario.bem.enums.EstadoConservacao;
import com.gestao.inventario.bem.enums.StatusBem;
import com.gestao.inventario.categoria.model.Categoria;
import com.gestao.inventario.localizacao.model.Localizacao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigoPatrimonio;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorAquisicao;

    private LocalDate dataAquisicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoConservacao estadoConservacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusBem status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "localizacao_id", nullable = false)
    private Localizacao localizacao;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "bem_categoria", joinColumns = @JoinColumn(name = "bem_id"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private Set<Categoria> categorias = new HashSet<>();
}