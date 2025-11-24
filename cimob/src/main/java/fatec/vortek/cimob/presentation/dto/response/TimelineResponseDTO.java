package fatec.vortek.cimob.presentation.dto.response;

import java.time.LocalDateTime;

import fatec.vortek.cimob.domain.enums.TimelineAcao;
import fatec.vortek.cimob.domain.enums.TimelineTipo;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import fatec.vortek.cimob.domain.model.Timeline;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimelineResponseDTO {
    private Long timelineId;
    private TimelineTipo tipo;
    private String emailUsuario;
    private TimelineAcao acao;
    private String descricao;
    private LocalDateTime data;

    public static TimelineResponseDTO TimelineModel2ResponseDTO(Timeline timeline) {
        TimelineResponseDTO dto = new TimelineResponseDTO();
        dto.timelineId = timeline.getTimelineId();
        dto.tipo = timeline.getTipo();
        dto.emailUsuario = timeline.getUsuario().getEmail();
        dto.acao = timeline.getAcao();
        dto.descricao = timeline.getDescricao();
        dto.data = timeline.getData();
        return dto;
    }
}
