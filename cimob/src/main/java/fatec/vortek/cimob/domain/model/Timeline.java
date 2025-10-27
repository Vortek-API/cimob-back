package fatec.vortek.cimob.domain.model;

import java.time.LocalDateTime;

import fatec.vortek.cimob.domain.converter.TimelineAcaoConverter;
import fatec.vortek.cimob.domain.converter.TimelineTipoConverter;
import fatec.vortek.cimob.domain.converter.TipoVeiculoConverter;
import fatec.vortek.cimob.domain.enums.TimelineAcao;
import fatec.vortek.cimob.domain.enums.TimelineTipo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Ponto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timelineId")
    private Long timelineId;

    @Column(name = "tipo")
    @Convert(converter = TimelineTipoConverter.class)
    private TimelineTipo tipo;

    @ManyToOne
    @JoinColumn(name = "usuarioId")
    private Usuario usuario;

    @Column(name = "acao")
    @Convert(converter = TimelineAcaoConverter.class)
    private TimelineAcao acao;

    @Column(name = "descricao", length = 100)
    private String descricao;

    @Column(name = "data")
    private LocalDateTime data;
}
