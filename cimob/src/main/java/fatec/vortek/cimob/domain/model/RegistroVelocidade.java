package fatec.vortek.cimob.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "regiaoId")
    private Regiao regiao;

    @Column(length = 50)
    private String tipoVeiculo;

    @Column
    private Integer velocidadeRegistrada;

    @Column
    private LocalDateTime data;

    @Column(length = 1)
    private char deletado = 'N';
}