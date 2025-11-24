package fatec.vortek.cimob.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Long eventoId;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    @Column(length = 255)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "usuarioId")
    private Usuario usuario;

    @Column(length = 1)
    @Builder.Default
    private String deletado = "N";

    @ManyToMany
    @JoinTable(
            name = "EventoRegiao",
            joinColumns = @JoinColumn(name = "eventoId"),
            inverseJoinColumns = @JoinColumn(name = "regiaoId")
    )
    @Builder.Default
    private List<Regiao> regioes = new ArrayList<>();
}
