package fatec.vortek.cimob.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

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

    @Column(length = 255)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "usuarioId")
    private Usuario usuario;

    @Column(length = 1)
    @Builder.Default
    private String deletado = "N";
    
    private LocalDateTime dataFim;

    @ManyToMany
    @JoinTable(
            name = "EventoRegiao",
            joinColumns = @JoinColumn(name = "eventoId"),
            inverseJoinColumns = @JoinColumn(name = "regiaoId")
    )
    @Builder.Default
    private List<Regiao> regioes = new ArrayList<Regiao>();
}
