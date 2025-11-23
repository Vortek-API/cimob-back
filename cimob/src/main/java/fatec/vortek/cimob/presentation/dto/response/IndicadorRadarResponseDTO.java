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
    IndicadorResponseDTO indicador;
    private String radarId;

    public static IndicadorRadarResponseDTO IndicadorModel2RadarResponseDTO(Indicador indicador, String radarId) {
        return new IndicadorRadarResponseDTO(
                IndicadorResponseDTO.IndicadorModel2ResponseDTO(indicador),
                radarId
        );
    }
}
