package fatec.vortek.cimob.domain.service;

import fatec.vortek.cimob.presentation.dto.request.RegistroVelocidadeRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.RegistroVelocidadeResponseDTO;
import java.util.List;

public interface RegistroVelocidadeService {
    RegistroVelocidadeResponseDTO criar(RegistroVelocidadeRequestDTO dto);
    RegistroVelocidadeResponseDTO buscarPorId(Long id);
    List<RegistroVelocidadeResponseDTO> listarTodos();
    void deletar(Long id);
}