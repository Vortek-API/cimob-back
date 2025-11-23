package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.domain.service.RegiaoService;
import fatec.vortek.cimob.infrastructure.repository.RegiaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegiaoServiceImpl implements RegiaoService {

    private final RegiaoRepository repository;

    @Override
    public Regiao criar(Regiao regiao) {
        return repository.save(regiao);
    }

    @Override
    public Regiao atualizar(Regiao regiao) {
        return repository.save(regiao);
    }

    @Override
    public void deletar(Long id) {
        repository.findById(id).ifPresent(r -> {
            r.setDeletado("S");
            repository.save(r);
        });
    }

    @Override
    public Regiao buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Regiao> listarTodos() {
        return repository.findAll();
    }
}
