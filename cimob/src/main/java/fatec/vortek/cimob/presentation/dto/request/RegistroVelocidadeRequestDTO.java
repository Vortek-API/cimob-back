package fatec.vortek.cimob.presentation.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegistroVelocidadeRequestDTO {
    private String radarId;
    private Long regiaoId;
    private String tipoVeiculo;
    private Integer velocidadeRegistrada;
    private LocalDateTime data;
}