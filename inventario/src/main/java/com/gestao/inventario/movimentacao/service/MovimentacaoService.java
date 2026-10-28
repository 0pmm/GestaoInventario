package com.gestao.inventario.movimentacao.service;

import com.gestao.inventario.bem.model.Bem;
import com.gestao.inventario.bem.repository.BemRepository;
import com.gestao.inventario.localizacao.model.Localizacao;
import com.gestao.inventario.movimentacao.dto.MovimentacaoResponse;
import com.gestao.inventario.movimentacao.enums.TipoMovimentacao;
import com.gestao.inventario.movimentacao.model.Movimentacao;
import com.gestao.inventario.movimentacao.repository.MovimentacaoRepository;
import com.gestao.inventario.usuario.service.UsuarioAtual;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoRepository repository;
    private final BemRepository bemRepository;
    private final UsuarioAtual usuarioAtual;

    @Transactional
    public void registrar(Bem bem, TipoMovimentacao tipo, String valorAnterior, String valorNovo,
            Localizacao origem, Localizacao destino, String motivo) {
        Movimentacao movimentacao = Movimentacao.builder()
                .bem(bem)
                .tipo(tipo)
                .dataHora(LocalDateTime.now())
                .valorAnterior(valorAnterior)
                .valorNovo(valorNovo)
                .origem(origem)
                .destino(destino)
                .motivo(motivo)
                .responsavel(usuarioAtual.obter().orElse(null))
                .build();
        repository.save(movimentacao);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listar(Long bemId, TipoMovimentacao tipo,
            LocalDate de, LocalDate ate, Long responsavelId) {
        if (bemId != null && !bemRepository.existsById(bemId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bem não encontrado: " + bemId);
        }

        boolean admin = usuarioAtual.ehAdministrador();
        List<TipoMovimentacao> tipos = admin
                ? List.of(TipoMovimentacao.values())
                : TipoMovimentacao.visiveisParaUsuarioComum();

        LocalDateTime inicio = (de == null) ? null : de.atStartOfDay();
        LocalDateTime fim = (ate == null) ? null : ate.plusDays(1).atStartOfDay();

        return repository.buscar(tipos, bemId, tipo, inicio, fim, admin ? responsavelId : null)
                .stream()
                .map(m -> MovimentacaoResponse.de(m, admin))
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimentacaoResponse buscarPorId(Long id) {
        Movimentacao movimentacao = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Movimentação não encontrada: " + id));

        boolean admin = usuarioAtual.ehAdministrador();
        if (!admin && !movimentacao.getTipo().isVisivelParaUsuarioComum()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimentação não encontrada: " + id);
        }
        return MovimentacaoResponse.de(movimentacao, admin);
    }
}