package fatec.vortek.cimob.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroVelocidadeListagemResponseDTO {
    private String registroVelocidadeId;
    private String radarId;
    private String regiaoId;
    private String tipoVeiculo;
    private String velocidade;
    private String velocidadePermitida;
    private String data;
    private String deletado;
}
