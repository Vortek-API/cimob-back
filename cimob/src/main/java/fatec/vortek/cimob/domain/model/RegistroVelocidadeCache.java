package fatec.vortek.cimob.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

import fatec.vortek.cimob.domain.converter.TipoVeiculoConverter;
import fatec.vortek.cimob.domain.enums.TipoVeiculo;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegistroVelocidadeCache {

    private Long registroVelocidadeId;
    private String radarId;
    private Long regiaoId;
    private TipoVeiculo tipoVeiculo;
    private Integer velocidadeRegistrada;
    private LocalDateTime data;
    private char deletado = 'N';

    public static  RegistroVelocidadeCache Model2ModelCache(RegistroVelocidade registroVelocidade) {
        return new RegistroVelocidadeCache(
                registroVelocidade.getRegistroVelocidadeId(),
                registroVelocidade.getRadar().getRadarId(),
                registroVelocidade.getRegiao().getRegiaoId(),
                registroVelocidade.getTipoVeiculo(),
                registroVelocidade.getVelocidadeRegistrada(),
                registroVelocidade.getData(),
                registroVelocidade.getDeletado()
        );
    }

    public static  RegistroVelocidade ModelCache2Model(RegistroVelocidadeCache registroVelocidadeCache) {
        RegistroVelocidade registro = new RegistroVelocidade();
        registro.setRegistroVelocidadeId(registroVelocidadeCache.getRegistroVelocidadeId());
        registro.setRadarId(registroVelocidadeCache.getRadarId());
        registro.setRegiaoId(registroVelocidadeCache.getRegiaoId());
        registro.setTipoVeiculo(registroVelocidadeCache.getTipoVeiculo());
        registro.setVelocidadeRegistrada(registroVelocidadeCache.getVelocidadeRegistrada());
        registro.setData(registroVelocidadeCache.getData());
        registro.setDeletado(registroVelocidadeCache.getDeletado());

        return registro;
    }
}