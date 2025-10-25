package fatec.vortek.cimob.presentation.dto.response;

import fatec.vortek.cimob.domain.enums.IndicadorMnemonico;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.model.RegistroVelocidade;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicadorResponseDTO {
    private Long indicadorId;
    private String nome;
    private Double valor;
    private IndicadorMnemonico mnemonico;
    private Boolean oculto;
    private String descricao;
    private Long usuarioId;

    public static IndicadorResponseDTO IndicadorModel2ResponseDTO(Indicador indicador) {
        return new IndicadorResponseDTO(
                indicador.getIndicadorId(),
                indicador.getNome(),
                indicador.getValor(),
                indicador.getMnemonico(),
                indicador.getOculto().equals("S"),
                indicador.getDescricao(),
                indicador.getUsuario() != null ? indicador.getUsuario().getUsuarioId() : null
        );
    }
}
