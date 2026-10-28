package com.gestao.inventario.categoria.dto;

import com.gestao.inventario.categoria.model.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaDto(

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao
) {
    public CategoriaDto {
        nome = nome == null ? null : nome.trim();
        descricao = (descricao == null || descricao.isBlank()) ? null : descricao.trim();
    }

    public static CategoriaDto de(Categoria categoria) {
        return new CategoriaDto(categoria.getId(), categoria.getNome(), categoria.getDescricao());
    }
}