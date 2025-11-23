package fatec.vortek.cimob.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import fatec.vortek.cimob.domain.enums.IndicadorMnemonico;

@Entity
@Table(name = "Indicador")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Indicador {

    @Id
    @SequenceGenerator(
        name = "seq_indicador",
        sequenceName = "seq_indicador",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_indicador")
    @Column(name = "indicadorId")
    private Long indicadorId;

    @Column(nullable = false, length = 100)
    private String nome;

    @Transient
    private Double valor;

    @Column(length = 100)
    @Enumerated(EnumType.STRING)
    private IndicadorMnemonico mnemonico;


    @Column(length = 255)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "usuarioId")
    private Usuario usuario;

    @Builder.Default
    @Column(length = 1, nullable = false)
    private String deletado = "N";

    @Builder.Default
    @Column(length = 1, nullable = false)
    private String oculto = "N";
}
