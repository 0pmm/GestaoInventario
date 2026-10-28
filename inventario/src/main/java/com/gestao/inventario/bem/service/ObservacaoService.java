package com.gestao.inventario.bem.service;

import com.gestao.inventario.bem.dto.ObservacaoRequest;
import com.gestao.inventario.bem.dto.ObservacaoResponse;
import com.gestao.inventario.bem.model.Bem;
import com.gestao.inventario.bem.model.Observacao;
import com.gestao.inventario.bem.repository.ObservacaoRepository;
import com.gestao.inventario.usuario.model.Usuario;
import com.gestao.inventario.usuario.service.UsuarioAtual;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObservacaoService {

    private final ObservacaoRepository repository;
    private final BemService bemService;
    private final UsuarioAtual usuarioAtual;

    @Transactional(readOnly = true)
    public List<ObservacaoResponse> listar(Long bemId) {
        bemService.buscarEntidade(bemId); 

        return repository.findByBemIdOrderByDataHoraDescIdDesc(bemId).stream()
                .map(ObservacaoResponse::de)
                .toList();
    }

    @Transactional
    public ObservacaoResponse criar(Long bemId, ObservacaoRequest request) {
        Bem bem = bemService.buscarEntidade(bemId);

        Usuario autor = usuarioAtual.obter()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Faça login para continuar"));

        Observacao observacao = Observacao.builder()
                .bem(bem)
                .texto(request.texto())
                .dataHora(LocalDateTime.now())
                .autor(autor)
                .build();

        return ObservacaoResponse.de(repository.save(observacao));
    }
}