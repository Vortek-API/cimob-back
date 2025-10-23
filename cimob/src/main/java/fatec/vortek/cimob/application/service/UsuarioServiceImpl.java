package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.model.Usuario;
import fatec.vortek.cimob.domain.service.UsuarioService;
import fatec.vortek.cimob.infrastructure.repository.UsuarioRepository;
import fatec.vortek.cimob.presentation.dto.request.UsuarioRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (repository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("Usuário já existe com CPF: " + dto.getCpf());
        }

        Usuario novoRegistro = new Usuario();
        novoRegistro.setNome(dto.getNome());
        novoRegistro.setCargo(dto.getCargo());
        novoRegistro.setDeletado(dto.getDeletado());
        novoRegistro.setEmail(dto.getEmail());

        // 🚀 Criptografa a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(dto.getSenha());
        novoRegistro.setSenha(senhaCriptografada);

        Usuario registroSalvo = repository.save(novoRegistro);
        return toResponseDTO(registroSalvo);
    }

    @Override
    public Usuario atualizar(Usuario usuario) {
        if (!repository.existsById(usuario.getUsuarioId())) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + usuario.getUsuarioId());
        }
        return repository.save(usuario);
    }

    @Override
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        repository.findById(id).ifPresent(r -> {
            r.setDeletado("S");
            repository.save(r);
        });
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id).orElse(null);
        return toResponseDTO(usuario);
    }

    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getUsuarioId(),
                usuario.getNome(),
                usuario.getCargo(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getDeletado()
        );
    }
}
