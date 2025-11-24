package fatec.vortek.cimob.domain.service;

import fatec.vortek.cimob.domain.enums.IndicadorMnemonico;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.presentation.dto.response.IndiceCriticoResponseDTO;

import java.util.List;

public interface IndicadorService {
    Indicador criar(Indicador indicador);
    Indicador atualizar(Indicador indicador);
    void deletar(Long id);
    Indicador buscarPorId(Long id);
    List<Indicador> listarTodos();
    List<Indicador> listarTodos(String timestamp);
    List<Indicador> listarPorRegiao(Long regiaoId);
    List<Indicador> listarPorRegiao(Long regiaoId, String timestamp);
    java.util.List<IndiceCriticoResponseDTO> listarTopExcessosVelocidade(Long regiaoId);
    java.util.List<IndiceCriticoResponseDTO> listarTopExcessosVelocidade(Long regiaoId, String timestamp);
    Indicador obterPorMnemonicoRegiao(IndicadorMnemonico mnemonico, Long regiaoId, String timestamp);
    Indicador obterPorMnemonico(IndicadorMnemonico mnemonico, String timestamp);
    List<Indicador> listarIndicadoresSemCalculo();
    void atualizaSelecionados(List<Long> indicadoresId);
    List<Indicador> listarPorRegiao(Long regiaoId, String timestamp, String radarId);
    List<Indicador> listarTodos(String timestamp, String radarId);
}
