package fatec.vortek.cimob.domain.service;

import fatec.vortek.cimob.domain.model.RegistroVelocidadeCache;
import fatec.vortek.cimob.presentation.dto.request.RegistroVelocidadeRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.RegistroVelocidadeResponseDTO;
import fatec.vortek.cimob.presentation.dto.response.RegistroVelocidadeListagemResponseDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface RegistroVelocidadeService {
    RegistroVelocidadeResponseDTO criar(RegistroVelocidadeRequestDTO dto);
    RegistroVelocidadeResponseDTO buscarPorId(Long id);
    List<RegistroVelocidadeResponseDTO> listarTodos();
    List<RegistroVelocidadeListagemResponseDTO> buscarPorFiltro(String radarId, Long regiaoId, boolean todasRegioes, LocalDateTime dataInicio, LocalDateTime dataFim);
    void deletar(Long id);
    List<RegistroVelocidadeCache> buscarRegistrosPorRegiao(Long regiaoId, String timestamp);
}
