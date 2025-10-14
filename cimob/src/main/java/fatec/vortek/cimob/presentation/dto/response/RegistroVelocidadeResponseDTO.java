package fatec.vortek.cimob.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

import fatec.vortek.cimob.domain.model.RegistroVelocidade;

@Data
@AllArgsConstructor
public class RegistroVelocidadeResponseDTO {
    private Long registroVelocidadeId;
    private String radarId;
    private Long regiaoId;
    private String tipoVeiculo;
    private Integer velocidadeRegistrada;
    private LocalDateTime data;


    public static RegistroVelocidadeResponseDTO RegistroVelocidadeModel2ResponseDTO(RegistroVelocidade registroVelocidade)
    {
        return new RegistroVelocidadeResponseDTO(registroVelocidade.getRegistroVelocidadeId(), 
        registroVelocidade.getRadar().getRadarId(), registroVelocidade.getRegiao().getRegiaoId(), 
        registroVelocidade.getTipoVeiculo(), 
        registroVelocidade.getVelocidadeRegistrada(),
        registroVelocidade.getData());
    }
}