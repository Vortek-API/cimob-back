package fatec.vortek.cimob.presentation.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import fatec.vortek.cimob.domain.model.Evento;

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
    private Long usuarioId;
    private List<Long> regioesIds;

    public static EventoResponseDTO Model2ResponseDTO(Evento evento)
    {
        return new EventoResponseDTO(
                evento.getEventoId(),
                evento.getNome(),
                evento.getDescricao(),
                evento.getDataInicio(),
                evento.getDataFim(),
                evento.getUsuario() != null ? evento.getUsuario().getUsuarioId() : null,
                evento.getRegioes() != null ? evento.getRegioes().stream().map(r -> r.getRegiaoId()).collect(Collectors.toList()) : null
        );
    }
}
