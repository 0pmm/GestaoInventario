package com.gestao.inventario.usuario.dto;


import com.gestao.inventario.usuario.enums.Perfil;
import com.gestao.inventario.usuario.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        Boolean ativo
) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getPerfil(),
            usuario.getAtivo());
    }
}
