package fatec.vortek.cimob.presentation.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

import fatec.vortek.cimob.domain.enums.TipoVeiculo;

@Data
public class RegistroVelocidadeRequestDTO {
    private String radarId;
    private Long regiaoId;
    private TipoVeiculo tipoVeiculo;
    private Integer velocidadeRegistrada;
    private LocalDateTime data;
}