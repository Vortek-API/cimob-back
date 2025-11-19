package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.model.Radar;
import fatec.vortek.cimob.domain.service.RadarService;
import fatec.vortek.cimob.infrastructure.repository.RadarRepository;
import fatec.vortek.cimob.presentation.dto.response.IndicadorRadarResponseDTO;
import fatec.vortek.cimob.presentation.dto.response.IndicadorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RadarServiceImpl implements RadarService {

    private final RadarRepository repository;
    private final IndicadorServiceImpl indicadorService;

    @Override
    public Radar criar(Radar radar) {
        return repository.save(radar);
    }

    @Override
    public Radar atualizar(Radar radar) {
        return repository.save(radar);
    }

    @Override
    public void deletar(String id) {
        repository.findById(id).ifPresent(r -> {
            r.setDeletado("S");
            repository.save(r);
        });
    }

    @Override
    public List<IndicadorRadarResponseDTO> listarIndicadores(String timestamp, String radarId)
    {
        List<Indicador> indicadores = indicadorService.listarTodos(timestamp).stream()
                    .filter(i -> !"S".equals(i.getDeletado()))
                    .collect(Collectors.toList());


        return indicadores.stream()
                .map(i -> IndicadorRadarResponseDTO.IndicadorModel2RadarResponseDTO(i, radarId))
                .collect(Collectors.toList());
    }

    @Override
    public Radar buscarPorId(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Radar> listarTodos() {
        return repository.findAll();
    }
}
