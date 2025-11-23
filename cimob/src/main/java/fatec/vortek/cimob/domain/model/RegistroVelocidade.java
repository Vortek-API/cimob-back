package fatec.vortek.cimob.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

import fatec.vortek.cimob.domain.converter.TipoVeiculoConverter;
import fatec.vortek.cimob.domain.enums.TipoVeiculo;

@Entity
@Table(name = "RegistroVelocidade")
@Data
@EqualsAndHashCode(of = "registroVelocidadeId")

@SQLDelete(sql = "UPDATE RegistroVelocidade SET deletado = 'S' WHERE registroVelocidadeId = ?")
@SQLRestriction("deletado = 'N'")
public class RegistroVelocidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registroVelocidadeId")
    private Long registroVelocidadeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "radarId", nullable = false)
    private Radar radar;

    @Transient
    private String radarId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "regiaoId")
    private Regiao regiao;

    @Transient
    private Long regiaoId;

    @Column(name = "tipoVeiculo")
    @Convert(converter = TipoVeiculoConverter.class)
    private TipoVeiculo tipoVeiculo;

    @Column
    private Integer velocidadeRegistrada;

    @Column
    private LocalDateTime data;

    @Column(length = 1)
    private char deletado = 'N';
}