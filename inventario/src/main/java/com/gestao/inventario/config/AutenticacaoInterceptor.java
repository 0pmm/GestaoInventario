package com.gestao.inventario.config;

import com.gestao.inventario.usuario.enums.Perfil;
import com.gestao.inventario.usuario.model.Usuario;
import com.gestao.inventario.usuario.repository.UsuarioRepository;
import com.gestao.inventario.usuario.service.UsuarioAtual;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AutenticacaoInterceptor implements HandlerInterceptor {

    private final UsuarioAtual usuarioAtual;
    private final UsuarioRepository usuarioRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String metodo = request.getMethod();
        String caminho = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            return true;
        }

        if (caminho.equals("/api/auth/login") || caminho.equals("/api/auth/logout")) {
            return true;
        }

        if ("POST".equalsIgnoreCase(metodo) && caminho.equals("/api/usuarios")
                && usuarioRepository.count() == 0) {
            return true;
        }

        Usuario usuario = usuarioAtual.obter()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Faça login para continuar"));

        if (exigeAdministrador(metodo, caminho) && usuario.getPerfil() != Perfil.ADMINISTRADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso restrito a administradores");
        }
        return true;
    }

    private boolean exigeAdministrador(String metodo, String caminho) {
        if (caminho.startsWith("/api/usuarios")) {
            return true;
        }
        if (caminho.startsWith("/api/auth")) {
            return false;
        }
        boolean leitura = metodo.equalsIgnoreCase("GET") || metodo.equalsIgnoreCase("HEAD");
        boolean observacao = caminho.matches("/api/bens/\\d+/observacoes/?");
        return !leitura && !observacao;
    }
}