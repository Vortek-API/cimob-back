package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.model.Usuario;
import fatec.vortek.cimob.domain.service.UsuarioService;
import fatec.vortek.cimob.infrastructure.repository.UsuarioRepository;
import fatec.vortek.cimob.presentation.dto.request.UsuarioRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.UsuarioResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.apache.catalina.mapper.Mapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
        novoRegistro.setCpf(dto.getCpf());

        String senhaCriptografada = passwordEncoder.encode(
            Objects.requireNonNullElse(dto.getSenha(), UUID.randomUUID().toString())
        );
        novoRegistro.setSenha(senhaCriptografada);

        Usuario registroSalvo = repository.save(novoRegistro);
        return toResponseDTO(registroSalvo);
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
                .filter(usuario -> !"S".equals(usuario.getDeletado()))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuarioExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));

        usuarioExistente.setNome(dto.getNome());
        usuarioExistente.setCargo(dto.getCargo());
        usuarioExistente.setEmail(dto.getEmail());
        usuarioExistente.setCpf(dto.getCpf());

        // Se a senha for fornecida no DTO, atualize-a
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            String senhaCriptografada = passwordEncoder.encode(dto.getSenha());
            usuarioExistente.setSenha(senhaCriptografada);
        }

        Usuario usuarioAtualizado = repository.save(usuarioExistente);
        return toResponseDTO(usuarioAtualizado);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getUsuarioId(),
                usuario.getNome(),
                usuario.getCargo(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getDeletado().equals("S")
        );
    }
@Override
public UsuarioResponseDTO buscarPorEmail(String email) {
    Usuario usuario = repository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

    UsuarioResponseDTO dto = new UsuarioResponseDTO();
    dto.setNome(usuario.getNome());
    dto.setEmail(usuario.getEmail());
    dto.setCargo(usuario.getCargo());
    return dto;
}
}
