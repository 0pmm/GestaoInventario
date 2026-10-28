package com.gestao.inventario.localizacao.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gestao.inventario.localizacao.dto.LocalizacaoDto;
import com.gestao.inventario.localizacao.service.LocalizacaoService;

import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping ("/api/localizacao")
@RequiredArgsConstructor 
@Tag(name = "Localização", description = "Gerenciamento das localizações da instituição")
public class LocalizacaoController {
    
    private final LocalizacaoService service;
    
    @GetMapping
    @Operation(summary =  "Lista as localizações (opcionalmente filtrado pelo nome)")
    public List<LocalizacaoDto> listar(@RequestParam(required = false) String nome) {
        return service.listar(nome);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma localização por id")
    public LocalizacaoDto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova localização")
    public ResponseEntity<LocalizacaoDto> criar(@Valid @RequestBody LocalizacaoDto dto) {
        LocalizacaoDto criada = service.criar(dto);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma localização")
    public LocalizacaoDto atualizar (@PathVariable Long id, @Valid @RequestBody LocalizacaoDto dto) {
        return service.atualizar(id, dto);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma localização")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
            service.excluir(id);
            return ResponseEntity.noContent().build();
    }
}
