package fatec.vortek.cimob.presentation.dto.response;

import lombok.*;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Regiao;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoResponseDTO {

    private Long eventoId;
    private String nome;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private List<RegiaoResponseDTO> regioes;

    public static EventoResponseDTO Model2ResponseDTO(Evento evento) {
        return new EventoResponseDTO(
                evento.getEventoId(),
                evento.getNome(),
                evento.getDescricao(),
                evento.getDataInicio(),
                evento.getDataFim(),
                evento.getRegioes().stream()
                        .map(regiao -> new RegiaoResponseDTO(regiao.getRegiaoId(), regiao.getNome()))
                        .collect(Collectors.toList())
        );
    }
}
