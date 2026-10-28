package com.gestao.inventario.usuario.service;

import com.gestao.inventario.bem.repository.ObservacaoRepository;
import com.gestao.inventario.movimentacao.repository.MovimentacaoRepository;
import com.gestao.inventario.usuario.dto.UsuarioRequest;
import com.gestao.inventario.usuario.dto.UsuarioResponse;
import com.gestao.inventario.usuario.enums.Perfil;
import com.gestao.inventario.usuario.model.Usuario;
import com.gestao.inventario.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final ObservacaoRepository observacaoRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(String nome) {
        List<Usuario> usuarios = (nome == null || nome.isBlank())
                ? repository.findAll(Sort.by("nome"))
                : repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome.trim());

        return usuarios.stream().map(UsuarioResponse::de).toList();
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        if (request.senha() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A senha é obrigatória");
        }
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um usuário com o e-mail '" + request.email() + "'");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .perfil(request.perfil())
                .build();

        return UsuarioResponse.de(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.de(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);

        if (repository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um usuário com o e-mail '" + request.email() + "'");
        }

        if (usuario.getPerfil() == Perfil.ADMINISTRADOR && request.perfil() != Perfil.ADMINISTRADOR) {
            garantirQueNaoEhUltimoAdmin(usuario);
        }

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setPerfil(request.perfil());
        if (request.senha() != null) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }

        return UsuarioResponse.de(repository.save(usuario));
    }

    @Transactional
    public UsuarioResponse inativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        garantirQueNaoEhUltimoAdmin(usuario);
        usuario.setAtivo(false);
        return UsuarioResponse.de(repository.save(usuario));
    }

    @Transactional
    public UsuarioResponse reativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(true);
        return UsuarioResponse.de(repository.save(usuario));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarEntidade(id);
        garantirQueNaoEhUltimoAdmin(usuario);

        if (movimentacaoRepository.existsByResponsavelId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Usuário com ações no histórico não pode ser excluído. Inative-o.");
        }
        if (observacaoRepository.existsByAutorId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Usuário com observações registradas não pode ser excluído. Inative-o.");
        }

        repository.delete(usuario);
    }

    public Usuario buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuário não encontrado: " + id));
    }

    private void garantirQueNaoEhUltimoAdmin(Usuario usuario) {
        boolean administradorAtivo = usuario.getPerfil() == Perfil.ADMINISTRADOR && usuario.getAtivo();
        if (administradorAtivo && repository.countByPerfilAndAtivoTrue(Perfil.ADMINISTRADOR) <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O sistema precisa ter ao menos um administrador ativo");
        }
    }

}
