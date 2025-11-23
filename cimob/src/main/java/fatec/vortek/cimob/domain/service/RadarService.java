package fatec.vortek.cimob.domain.service;

import fatec.vortek.cimob.domain.model.Radar;
import java.util.List;

public interface RadarService {
    Radar criar(Radar radar);
    Radar atualizar(Radar radar);
    void deletar(String id);
    Radar buscarPorId(String id);
    List<Radar> listarTodos();
}
