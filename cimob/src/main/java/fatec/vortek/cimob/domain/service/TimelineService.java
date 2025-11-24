
package fatec.vortek.cimob.domain.service;

import java.util.List;

import fatec.vortek.cimob.domain.enums.TimelineAcao;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.model.Timeline;
import fatec.vortek.cimob.domain.model.Usuario;

public interface TimelineService {

    Timeline criar(Timeline timeline);

    Timeline atualizar(Timeline timeline);
    
    Timeline buscarPorId(Long id);

    List<Timeline> listarTodos();

    Timeline criarTimelineLogin(Usuario usuario);

    Timeline criarTimelineLogout(Usuario usuario);

    Timeline criarTimelineIndicador(String descricao);

    Timeline criarTimelineIndicadorOculto(Indicador indicador);

    Timeline criarTimelineIndicadorDesoculto(Indicador indicador);

    Timeline criarTimelineCriacaoEvento(Evento evento);

    Timeline criarTimelineEvento(String descricao, TimelineAcao acao);

    Timeline criarTimelineExclusaoEvento(Evento evento);

    Timeline criarTimelineAlteracaoEvento(Evento evento);

}
