package fatec.vortek.cimob.domain.service;

import java.util.List;

import fatec.vortek.cimob.domain.model.Usuario;
import fatec.vortek.cimob.presentation.dto.request.UsuarioRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.UsuarioResponseDTO;

public interface UsuarioService {
    UsuarioResponseDTO criar(UsuarioRequestDTO dto);
    UsuarioResponseDTO buscarPorId(Long id);
    List<UsuarioResponseDTO> listarTodos();
    void deletar(Long id);
    UsuarioResponseDTO buscarPorEmail(String email);
    UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto);

}
