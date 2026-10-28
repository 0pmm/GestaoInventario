package com.gestao.inventario.bem.service;

import com.gestao.inventario.bem.dto.BemRequest;
import com.gestao.inventario.bem.dto.BemResponse;
import com.gestao.inventario.bem.enums.EstadoConservacao;
import com.gestao.inventario.bem.enums.StatusBem;
import com.gestao.inventario.bem.model.Bem;
import com.gestao.inventario.bem.repository.BemRepository;
import com.gestao.inventario.bem.repository.ObservacaoRepository;
import com.gestao.inventario.categoria.model.Categoria;
import com.gestao.inventario.categoria.service.CategoriaService;
import com.gestao.inventario.localizacao.model.Localizacao;
import com.gestao.inventario.localizacao.service.LocalizacaoService;
import com.gestao.inventario.movimentacao.enums.TipoMovimentacao;
import com.gestao.inventario.movimentacao.repository.MovimentacaoRepository;
import com.gestao.inventario.movimentacao.service.MovimentacaoService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BemService {

    private final BemRepository repository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final ObservacaoRepository observacaoRepository;
    private final CategoriaService categoriaService;
    private final LocalizacaoService localizacaoService;
    private final MovimentacaoService movimentacaoService;

    @Transactional(readOnly = true)
    public List<BemResponse> listar(String busca, Long categoriaId, Long localizacaoId,
            StatusBem status, LocalDate aquisicaoDe, LocalDate aquisicaoAte) {
        String termo = (busca == null) ? "" : busca.trim();

        return repository.buscar(termo, categoriaId, localizacaoId, status,
                StatusBem.INATIVO, aquisicaoDe, aquisicaoAte)
                .stream()
                .map(BemResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public BemResponse buscarPorId(Long id) {
        return BemResponse.de(buscarEntidade(id));
    }

    @Transactional
    public BemResponse criar(BemRequest request) {
        if (repository.existsByCodigoPatrimonioIgnoreCase(request.codigoPatrimonio())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um bem com o código '" + request.codigoPatrimonio() + "'");
        }

        Bem bem = Bem.builder()
                .status(request.status() != null ? request.status() : StatusBem.ATIVO)
                .build();
        preencher(bem, request);
        Bem salvo = repository.save(bem);

        movimentacaoService.registrar(salvo, TipoMovimentacao.CADASTRO,
                null,
                salvo.getNome() + " (" + salvo.getCodigoPatrimonio() + ")",
                null, salvo.getLocalizacao(), request.motivo());

        return BemResponse.de(salvo);
    }

    @Transactional
    public BemResponse atualizar(Long id, BemRequest request) {
        Bem bem = buscarEntidade(id);

        if (repository.existsByCodigoPatrimonioIgnoreCaseAndIdNot(request.codigoPatrimonio(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um bem com o código '" + request.codigoPatrimonio() + "'");
        }

        Foto antes = Foto.de(bem);

        preencher(bem, request);
        if (request.status() != null) {
            bem.setStatus(request.status());
        }
        Bem salvo = repository.save(bem);

        registrarAlteracoes(antes, salvo, request.motivo());
        return BemResponse.de(salvo);
    }

    @Transactional
    public BemResponse inativar(Long id, String motivo) {
        Bem bem = buscarEntidade(id);

        if (bem.getStatus() == StatusBem.INATIVO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O bem já está inativo");
        }

        StatusBem anterior = bem.getStatus();
        bem.setStatus(StatusBem.INATIVO);
        Bem salvo = repository.save(bem);

        movimentacaoService.registrar(salvo, TipoMovimentacao.INATIVACAO,
                anterior.name(), StatusBem.INATIVO.name(), null, null,
                (motivo == null || motivo.isBlank()) ? null : motivo.trim());

        return BemResponse.de(salvo);
    }

    @Transactional
    public void excluir(Long id) {
        Bem bem = buscarEntidade(id);

        if (movimentacaoRepository.existsByBemId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Bem com histórico de movimentação não pode ser excluído. Inative-o.");
        }
        if (movimentacaoRepository.existsByBemId(id) || observacaoRepository.existsByBemId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Bem com histórico ou observações não pode ser excluído. Inative-o.");
        }

        repository.delete(bem);
    }

    public Bem buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Bem não encontrado: " + id));
    }

    private void preencher(Bem bem, BemRequest request) {
        bem.setCodigoPatrimonio(request.codigoPatrimonio());
        bem.setNome(request.nome());
        bem.setDescricao(request.descricao());
        bem.setQuantidade(request.quantidade());
        bem.setValorAquisicao(request.valorAquisicao());
        bem.setDataAquisicao(request.dataAquisicao());
        bem.setEstadoConservacao(request.estadoConservacao());
        bem.setLocalizacao(localizacaoService.buscarEntidade(request.localizacaoId()));

        Set<Categoria> categorias = request.categoriaIds().stream()
                .map(categoriaService::buscarEntidade)
                .collect(Collectors.toSet());
        bem.getCategorias().clear();
        bem.getCategorias().addAll(categorias);
    }

    private void registrarAlteracoes(Foto antes, Bem depois, String motivo) {

        if (!antes.localizacao().getId().equals(depois.getLocalizacao().getId())) {
            movimentacaoService.registrar(depois, TipoMovimentacao.TRANSFERENCIA,
                    antes.localizacao().getNome(), depois.getLocalizacao().getNome(),
                    antes.localizacao(), depois.getLocalizacao(), motivo);
        }

        if (!antes.categoriaIds().equals(ids(depois.getCategorias()))) {
            movimentacaoService.registrar(depois, TipoMovimentacao.TROCA_CATEGORIA,
                    antes.categoriaNomes(), nomes(depois.getCategorias()),
                    null, null, motivo);
        }

        if (antes.estado() != depois.getEstadoConservacao()) {
            TipoMovimentacao tipo = (depois.getEstadoConservacao() == EstadoConservacao.RUIM)
                    ? TipoMovimentacao.AVARIA
                    : TipoMovimentacao.MUDANCA_ESTADO;
            movimentacaoService.registrar(depois, tipo,
                    antes.estado().name(), depois.getEstadoConservacao().name(),
                    null, null, motivo);
        }

        if (antes.status() != depois.getStatus()) {
            TipoMovimentacao tipo = switch (depois.getStatus()) {
                case VENDIDO -> TipoMovimentacao.VENDA;
                case EM_MANUTENCAO -> TipoMovimentacao.MANUTENCAO;
                case INATIVO -> TipoMovimentacao.INATIVACAO;
                case ATIVO -> TipoMovimentacao.MUDANCA_STATUS;
            };
            movimentacaoService.registrar(depois, tipo,
                    antes.status().name(), depois.getStatus().name(),
                    null, null, motivo);
        }

        List<String> de = new ArrayList<>();
        List<String> para = new ArrayList<>();
        comparar("código", antes.codigoPatrimonio(), depois.getCodigoPatrimonio(), de, para);
        comparar("nome", antes.nome(), depois.getNome(), de, para);
        comparar("descrição", antes.descricao(), depois.getDescricao(), de, para);
        comparar("quantidade", antes.quantidade(), depois.getQuantidade(), de, para);
        comparar("valor", antes.valorAquisicao(), depois.getValorAquisicao(), de, para);
        comparar("data de aquisição", antes.dataAquisicao(), depois.getDataAquisicao(), de, para);

        if (!de.isEmpty()) {
            movimentacaoService.registrar(depois, TipoMovimentacao.EDICAO_DADOS,
                    String.join("; ", de), String.join("; ", para),
                    null, null, motivo);
        }
    }

    private void comparar(String campo, Object antes, Object depois, List<String> de, List<String> para) {
        if (!iguais(antes, depois)) {
            de.add(campo + ": " + texto(antes));
            para.add(campo + ": " + texto(depois));
        }
    }

    private boolean iguais(Object a, Object b) {
        if (a instanceof BigDecimal x && b instanceof BigDecimal y) {
            return x.compareTo(y) == 0;
        }
        return Objects.equals(a, b);
    }

    private String texto(Object valor) {
        return valor == null ? "(vazio)" : valor.toString();
    }

    private static Set<Long> ids(Set<Categoria> categorias) {
        return categorias.stream().map(Categoria::getId).collect(Collectors.toSet());
    }

    private static String nomes(Set<Categoria> categorias) {
        return categorias.stream()
                .map(Categoria::getNome)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(", "));
    }

    private record Foto(String codigoPatrimonio, String nome, String descricao, Integer quantidade,
            BigDecimal valorAquisicao, LocalDate dataAquisicao,
            EstadoConservacao estado, StatusBem status,
            Localizacao localizacao, Set<Long> categoriaIds, String categoriaNomes) {

        static Foto de(Bem b) {
            return new Foto(b.getCodigoPatrimonio(), b.getNome(), b.getDescricao(), b.getQuantidade(),
                    b.getValorAquisicao(), b.getDataAquisicao(), b.getEstadoConservacao(), b.getStatus(),
                    b.getLocalizacao(), ids(b.getCategorias()), nomes(b.getCategorias()));
        }
    }
}