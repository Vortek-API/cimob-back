package fatec.vortek.cimob.domain.service;

import fatec.vortek.cimob.domain.model.Ponto;
import fatec.vortek.cimob.presentation.dto.response.PontoResponseDTO;
import fatec.vortek.cimob.infrastructure.repository.PontoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

public interface PontoService {
    List<PontoResponseDTO> listarTodos();
    List<PontoResponseDTO> listarTodosPorRegiaoId(Long regiaoId);
}
