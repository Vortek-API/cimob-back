package fatec.vortek.cimob.presentation.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoRequestDTO {
    private String nome;
    private String descricao;
    private Long usuarioId;
    private Long indicadorPrincipalId;
    private List<Long> indicadoresIds; // para N:N

    private List<Long> regioesIds;
}
