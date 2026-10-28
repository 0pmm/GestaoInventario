package com.gestao.inventario.localizacao.service;

import com.gestao.inventario.bem.repository.BemRepository;
import com.gestao.inventario.localizacao.dto.LocalizacaoDto;
import com.gestao.inventario.localizacao.model.Localizacao;
import com.gestao.inventario.localizacao.repository.LocalizacaoRepository;
import com.gestao.inventario.movimentacao.repository.MovimentacaoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalizacaoService {

    private final LocalizacaoRepository repostiory;
    private final BemRepository bemRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    @Transactional(readOnly = true)
    public List<LocalizacaoDto> listar(String nome) {
        List<Localizacao> localizacaos = (nome == null || nome.isBlank())
                ? repostiory.findAll(Sort.by("nome"))
                : repostiory.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome.trim());

        return localizacaos.stream().map(LocalizacaoDto::de).toList();
    }

    @Transactional(readOnly = true)
    public LocalizacaoDto buscarPorId(Long id) {
        return LocalizacaoDto.de(buscarEntidade(id));
    }

    @Transactional
    public LocalizacaoDto criar(LocalizacaoDto dto) {
        if (repostiory.existsByNomeIgnoreCase(dto.nome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe uma localização com o nome '" + dto.nome() + "'");
        }
        Localizacao localizacao = Localizacao.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .build();

        return LocalizacaoDto.de(repostiory.save(localizacao));
    }

    @Transactional
    public LocalizacaoDto atualizar(Long id, LocalizacaoDto dto) {
        Localizacao localizacao = buscarEntidade(id);

        if (repostiory.existsByNomeIgnoreCaseAndIdNot(dto.nome(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe uma localização com o nome '" + dto.nome() + "'");
        }

        localizacao.setNome(dto.nome());
        localizacao.setDescricao(dto.descricao());

        return LocalizacaoDto.de(repostiory.save(localizacao));
    }

    @Transactional
    public void excluir(Long id) {
        Localizacao localizacao = buscarEntidade(id);
        if (bemRepository.existsByLocalizacaoId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir: há bens nesta localização");
        }
        if (movimentacaoRepository.existsByOrigemIdOrDestinoId(id, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir: a localização consta no histórico de movimentações");
        }
        repostiory.delete(localizacao);
    }

    public Localizacao buscarEntidade(Long id) {
        return repostiory.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Localização não encontrada: " + id));
    }
}
