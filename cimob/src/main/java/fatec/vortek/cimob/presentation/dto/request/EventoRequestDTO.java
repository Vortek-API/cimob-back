package fatec.vortek.cimob.presentation.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoRequestDTO {

    private String nome;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    private Long usuarioId;
    private List<Long> regioesIds;
}
