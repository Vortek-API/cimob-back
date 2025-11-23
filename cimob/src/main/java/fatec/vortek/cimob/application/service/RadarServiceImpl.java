package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.model.Radar;
import fatec.vortek.cimob.domain.service.RadarService;
import fatec.vortek.cimob.infrastructure.repository.RadarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RadarServiceImpl implements RadarService {

    private final RadarRepository repository;

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
    public Radar buscarPorId(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Radar> listarTodos() {
        return repository.findAll();
    }
}
