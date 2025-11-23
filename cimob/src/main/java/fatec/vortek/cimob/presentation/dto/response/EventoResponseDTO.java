package fatec.vortek.cimob.presentation.dto.response;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoResponseDTO {
    private Long eventoId;
    private String nome;
    private String descricao;
    private Date data;
    private Long usuarioId;
    private Long indicadorPrincipalId;
    private List<Long> indicadoresIds;

    private List<Long> regioesIds;
}
