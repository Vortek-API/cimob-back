package fatec.vortek.cimob.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.service.EventoService;
import fatec.vortek.cimob.infrastructure.repository.EventoRepository;
import fatec.vortek.cimob.infrastructure.repository.IndicadorRepository;
import fatec.vortek.cimob.infrastructure.repository.RegiaoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final RegiaoRepository regiaoRepository;
    private final IndicadorRepository indicadorRepository;

    @Override
    public Evento criar(Evento evento) {
        // Se evento já vier com regioes configuradas, será salvo. Caso contrário, salva vazio.
        return eventoRepository.save(evento);
    }

    @Override
    public Evento atualizar(Evento evento) {
        if (evento.getEventoId() == null) {
            throw new IllegalArgumentException("EventoId é requerido para atualização");
        }
        Evento existente = eventoRepository.findById(evento.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento não encontrado: " + evento.getEventoId()));
        // Atualiza campos básicos
        existente.setNome(evento.getNome());
        existente.setDescricao(evento.getDescricao());
        existente.setData(evento.getData());
        existente.setUsuario(evento.getUsuario());
        existente.setDeletado(evento.getDeletado());

        // Se regioes vierem preenchidas no 'evento', substitui a lista
        if (evento.getRegioes() != null) {
            existente.setRegioes(evento.getRegioes());
        }

        // Se indicadores vierem, atualiza também (mantendo compatibilidade)
        if (evento.getIndicadores() != null) {
            existente.setIndicadores(evento.getIndicadores());
        }

        return eventoRepository.save(existente);
    }

    @Override
    public void deletar(Long id) {
        // Soft delete: marcar deletado = 'S' se quiser preservar histórico
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isPresent()) {
            Evento e = opt.get();
            e.setDeletado("S");
            eventoRepository.save(e);
        } else {
            throw new RuntimeException("Evento não encontrado: " + id);
        }
    }

    @Override
    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado: " + id));
    }

    @Override
    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    @Override
    public void associarIndicador(Long eventoId, Long indicadorId) {
        Evento e = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado: " + eventoId));
        Indicador i = indicadorRepository.findById(indicadorId)
                .orElseThrow(() -> new RuntimeException("Indicador não encontrado: " + indicadorId));
        List<Indicador> lista = e.getIndicadores();
        if (lista == null) {
            lista = new java.util.ArrayList<>();
        }
        if (!lista.contains(i)) {
            lista.add(i);
            e.setIndicadores(lista);
            eventoRepository.save(e);
        }
    }

    @Override
    public void desassociarIndicador(Long eventoId, Long indicadorId) {
        Evento e = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado: " + eventoId));
        Indicador i = indicadorRepository.findById(indicadorId)
                .orElseThrow(() -> new RuntimeException("Indicador não encontrado: " + indicadorId));
        List<Indicador> lista = e.getIndicadores();
        if (lista != null && lista.removeIf(ind -> ind.getIndicadorId().equals(indicadorId))) {
            e.setIndicadores(lista);
            eventoRepository.save(e);
        }
    }

    @Override
    public List<Indicador> listarIndicadores(Long eventoId) {
        Evento e = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado: " + eventoId));
        return e.getIndicadores();
    }
}
