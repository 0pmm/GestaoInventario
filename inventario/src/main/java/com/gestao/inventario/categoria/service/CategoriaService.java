package com.gestao.inventario.categoria.service;

import com.gestao.inventario.bem.repository.BemRepository;
import com.gestao.inventario.categoria.dto.CategoriaDto;
import com.gestao.inventario.categoria.model.Categoria;
import com.gestao.inventario.categoria.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;
    private final BemRepository bemRepository;

    @Transactional(readOnly = true)
    public List<CategoriaDto> listar(String nome) {
        List<Categoria> categorias = (nome == null || nome.isBlank())
                ? repository.findAll(Sort.by("nome"))
                : repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome.trim());

        return categorias.stream().map(CategoriaDto::de).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaDto buscarPorId(Long id) {
        return CategoriaDto.de(buscarEntidade(id));
    }

    @Transactional
    public CategoriaDto criar(CategoriaDto dto) {
        if (repository.existsByNomeIgnoreCase(dto.nome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe uma categoria com o nome '" + dto.nome() + "'");
        }

        Categoria categoria = Categoria.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .build();

        return CategoriaDto.de(repository.save(categoria));
    }

    @Transactional
    public CategoriaDto atualizar(Long id, CategoriaDto dto) {
        Categoria categoria = buscarEntidade(id);

        if (repository.existsByNomeIgnoreCaseAndIdNot(dto.nome(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe uma categoria com o nome '" + dto.nome() + "'");
        }

        categoria.setNome(dto.nome());
        categoria.setDescricao(dto.descricao());

        return CategoriaDto.de(repository.save(categoria));
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarEntidade(id);

        if (bemRepository.existsByCategoriasId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir: há bens com a categoria '" + categoria.getNome() + "'");
        }

        repository.delete(categoria);
    }

    public Categoria buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Categoria não encontrada: " + id));
    }
}