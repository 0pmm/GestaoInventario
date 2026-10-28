package com.gestao.inventario.localizacao.dto;

import com.gestao.inventario.localizacao.model.Localizacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocalizacaoDto(
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    Long id,

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    String nome,

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracters")
    String descricao
) {

    public LocalizacaoDto {
        nome = nome == null ? null : nome.trim();
        descricao = (descricao == null || descricao.isBlank()) ? null : descricao.trim();
    }

    public static LocalizacaoDto de (Localizacao localizacao) {
        return  new LocalizacaoDto(localizacao.getId(), localizacao.getNome(), localizacao.getDescricao());
    }
} 
