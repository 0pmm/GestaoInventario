package com.gestao.inventario.usuario.controller;

import com.gestao.inventario.usuario.dto.LoginRequest;
import com.gestao.inventario.usuario.dto.UsuarioResponse;
import com.gestao.inventario.usuario.model.Usuario;
import com.gestao.inventario.usuario.service.AuthService;
import com.gestao.inventario.usuario.service.UsuarioAtual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login, logout e usuário logado")
public class AuthController {

    private final AuthService service;
    private final UsuarioAtual usuarioAtual;

    @PostMapping("/login")
    @Operation(summary = "Faz login e cria a sessão (cookie)")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        Usuario usuario = service.autenticar(request);

        HttpSession sessao = http.getSession(true);
        http.changeSessionId();
        sessao.setAttribute(UsuarioAtual.SESSAO_USUARIO_ID, usuario.getId());

        return UsuarioResponse.de(usuario);
    }

    @PostMapping("/logout")
    @Operation(summary = "Encerra a sessão")
    public ResponseEntity<Void> logout(HttpServletRequest http) {
        HttpSession sessao = http.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Devolve o usuário logado (o Vue chama ao abrir a página)")
    public UsuarioResponse me() {
        return usuarioAtual.obter()
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Faça login para continuar"));
    }
}