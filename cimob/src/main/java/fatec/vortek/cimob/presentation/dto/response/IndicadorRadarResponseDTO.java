package fatec.vortek.cimob.presentation.dto.response;

import fatec.vortek.cimob.domain.enums.IndicadorMnemonico;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.model.RegistroVelocidade;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicadorRadarResponseDTO {
    private Long indicadorId;
    private String nome;
    private Double valor;
    private IndicadorMnemonico mnemonico;
    private Boolean oculto;
    private String descricao;
    private Long usuarioId;
    private String radarId;

    public static IndicadorRadarResponseDTO IndicadorModel2RadarResponseDTO(Indicador indicador, String radarId) {
        return new IndicadorRadarResponseDTO(
                indicador.getIndicadorId(),
                indicador.getNome(),
                indicador.getValor(),
                indicador.getMnemonico(),
                indicador.getOculto().equals("S"),
                indicador.getDescricao(),
                indicador.getUsuario() != null ? indicador.getUsuario().getUsuarioId() : null,
                radarId
        );
    }
}
