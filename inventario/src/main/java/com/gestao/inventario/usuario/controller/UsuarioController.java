package com.gestao.inventario.usuario.controller;

import com.gestao.inventario.usuario.dto.UsuarioRequest;
import com.gestao.inventario.usuario.dto.UsuarioResponse;
import com.gestao.inventario.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento dos usuários do sistema")
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    @Operation(summary = "Lista os usuários (opcionalmente filtrando pelo nome)")
    public List<UsuarioResponse> listar(@RequestParam(required = false) String nome) {
        return service.listar(nome);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuário pelo id")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo usuário")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse criado = service.criar(request);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário (senha vazia mantém a atual)")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativa um usuário")
    public UsuarioResponse inativar(@PathVariable Long id) {
        return service.inativar(id);
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativa um usuário")
    public UsuarioResponse reativar(@PathVariable Long id) {
        return service.reativar(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um usuário (só se não tiver histórico)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}