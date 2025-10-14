package fatec.vortek.cimob.presentation.dto.request;

import fatec.vortek.cimob.domain.enums.IndicadorMnemonico;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicadorRequestDTO {
    private String nome;
    private IndicadorMnemonico mnemonico;
    private String descricao;
    private Long usuarioId;
}
