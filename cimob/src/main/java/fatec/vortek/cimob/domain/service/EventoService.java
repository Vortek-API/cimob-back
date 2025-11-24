package fatec.vortek.cimob.domain.service;

import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Indicador;

import java.util.List;

public interface EventoService {
    Evento criar(Evento evento);
    Evento atualizar(Evento evento);
    void deletar(Long id);
    Evento buscarPorId(Long id);
    List<Evento> listarTodos();
}
