package com.gestao.inventario.usuario.service;

import com.gestao.inventario.usuario.enums.Perfil;
import com.gestao.inventario.usuario.model.Usuario;
import com.gestao.inventario.usuario.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioAtual {

    public static final String SESSAO_USUARIO_ID = "usuarioId";

    private final HttpServletRequest request;
    private final UsuarioRepository usuarioRepository;

    public Optional<Usuario> obter() {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) {
            return Optional.empty();
        }
        if (!(sessao.getAttribute(SESSAO_USUARIO_ID) instanceof Long usuarioId)) {
            return Optional.empty();
        }
        return usuarioRepository.findById(usuarioId).filter(Usuario::getAtivo);
    }

    public boolean ehAdministrador() {
        return obter().map(u -> u.getPerfil() == Perfil.ADMINISTRADOR).orElse(false);
    }
}