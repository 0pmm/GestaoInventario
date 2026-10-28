package com.gestao.inventario.bem.dto;

import com.gestao.inventario.bem.enums.EstadoConservacao;
import com.gestao.inventario.bem.enums.StatusBem;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record BemRequest(

        @NotBlank(message = "O código de patrimônio é obrigatório") @Size(max = 50, message = "O código deve ter no máximo 50 caracteres") String codigoPatrimonio,

        @NotBlank(message = "O nome é obrigatório") @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres") String nome,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres") String descricao,

        @NotNull(message = "A quantidade é obrigatória") @Min(value = 1, message = "A quantidade deve ser de pelo menos 1") Integer quantidade,

        @PositiveOrZero(message = "O valor não pode ser negativo") BigDecimal valorAquisicao,

        @PastOrPresent(message = "A data de aquisição não pode ser futura") LocalDate dataAquisicao,

        @NotNull(message = "O estado de conservação é obrigatório") EstadoConservacao estadoConservacao,

        StatusBem status,

        @NotNull(message = "A localização é obrigatória") Long localizacaoId,

        @NotEmpty(message = "Informe ao menos uma categoria") Set<@NotNull Long> categoriaIds,

        @Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres")
        String motivo)
        {
    public BemRequest {
        codigoPatrimonio = codigoPatrimonio == null ? null : codigoPatrimonio.trim();
        nome = nome == null ? null : nome.trim();
        descricao = (descricao == null || descricao.isBlank()) ? null : descricao.trim();
    }
}