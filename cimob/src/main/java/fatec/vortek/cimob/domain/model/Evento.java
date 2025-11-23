package fatec.vortek.cimob.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventoId")
    private Long eventoId;

    @Column(nullable = false, length = 100)
    private String nome;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date data = new Date();

    @Column(length = 255)
    private String descricao;

    
    @ManyToOne
    @JoinColumn(name = "usuarioId")
    private Usuario usuario;

    @Column(length = 1, nullable = false)
    private String deletado = "N";

    @ManyToMany
    @JoinTable(
            name = "EventoIndicador",
            joinColumns = @JoinColumn(name = "eventoId"),
            inverseJoinColumns = @JoinColumn(name = "indicadorId")
    )
    private List<Indicador> indicadores;

    @ManyToMany
    @JoinTable(
            name = "EventoRegiao",
            joinColumns = @JoinColumn(name = "eventoId"),
            inverseJoinColumns = @JoinColumn(name = "regiaoId")
    )
    private List<Regiao> regioes;
}
