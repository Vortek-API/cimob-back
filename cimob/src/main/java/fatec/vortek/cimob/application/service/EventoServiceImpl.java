package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.domain.service.EventoService;
import fatec.vortek.cimob.infrastructure.repository.EventoRepository;
import fatec.vortek.cimob.infrastructure.repository.RegiaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final RegiaoRepository regiaoRepository;

    @Override
    public Evento criar(Evento evento) {
        return eventoRepository.save(evento);
    }

    @Override
    public Evento atualizar(Evento evento) {
        Evento existente = eventoRepository.findById(evento.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        existente.setNome(evento.getNome());
        existente.setDescricao(evento.getDescricao());
        existente.setDataInicio(evento.getDataInicio());
        existente.setDataFim(evento.getDataFim());
        existente.setUsuario(evento.getUsuario());

        if (evento.getRegioes() != null) {
            existente.setRegioes(evento.getRegioes());
        }

        return eventoRepository.save(existente);
    }

    @Override
    public void deletar(Long id) {
        Evento e = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        e.setDeletado("S");
        eventoRepository.save(e);
    }

    @Override
    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    }

    @Override
    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }
}
