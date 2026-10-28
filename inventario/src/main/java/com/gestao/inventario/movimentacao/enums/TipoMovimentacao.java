package com.gestao.inventario.movimentacao.enums;

import java.util.Arrays;
import java.util.List;

public enum TipoMovimentacao {

    // Administrador vê
    CADASTRO(false),
    EDICAO_DADOS(false),
    INATIVACAO(false),

    // Usuário vê
    TRANSFERENCIA(true),
    TROCA_CATEGORIA(true),
    AVARIA(true),
    MUDANCA_ESTADO(true),
    MANUTENCAO(true),
    VENDA(true),
    MUDANCA_STATUS(true);

    private final boolean visivelParaUsuarioComum;

    TipoMovimentacao(boolean visivelParaUsuarioComum) {
        this.visivelParaUsuarioComum = visivelParaUsuarioComum;
    }

    public boolean isVisivelParaUsuarioComum() {
        return visivelParaUsuarioComum;
    }

    public static List<TipoMovimentacao> visiveisParaUsuarioComum() {
        return Arrays.stream(values())
                .filter(TipoMovimentacao::isVisivelParaUsuarioComum)
                .toList();
    }
}