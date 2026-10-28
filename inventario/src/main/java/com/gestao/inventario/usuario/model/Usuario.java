package com.gestao.inventario.usuario.model;

import com.gestao.inventario.usuario.enums.Perfil;
import jakarta.persistence.*;
import lombok.*;

@Entity 
@Table(name = "usuario")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class Usuario {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;
}
