package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.model.Ponto;
import fatec.vortek.cimob.domain.service.PontoService;
import fatec.vortek.cimob.presentation.dto.response.PontoResponseDTO;
import fatec.vortek.cimob.infrastructure.repository.PontoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PontoServiceImpl implements PontoService {

    private final PontoRepository pontoRepository;

    public List<PontoResponseDTO> listarTodos() {
        List<Ponto> pontos = pontoRepository.findAll();

        return pontos.stream()
             .map(PontoResponseDTO::PontoModel2ResponseDTO)
             .collect(Collectors.toList());
    }

    public List<PontoResponseDTO> listarTodosPorRegiaoId(Long regiaoId) {
    List<Ponto> pontos = pontoRepository.findByRegiao_RegiaoId(regiaoId);

    return pontos.stream()
                 .map(PontoResponseDTO::PontoModel2ResponseDTO)
                 .collect(Collectors.toList());
}

}
