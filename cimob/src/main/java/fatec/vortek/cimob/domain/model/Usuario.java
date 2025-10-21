package fatec.vortek.cimob.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuarioId")
    private Long usuarioId;

    @Column(name = "cpf", nullable = false, length = 11)
    private String cpf;

    @Column(name = "nomeCompleto", nullable = false, length = 100)
    private String nome;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "cargo", nullable = false, length = 100)
    private String cargo;

    @Column(name = "deletado", nullable = false, length = 1)
    private String deletado = "N";

    @Column(name = "senha", nullable = false, length = 255)
    private String senha; 

    @Column(name = "refreshToken")
    private String refreshToken;

    @Column(name = "accessToken")
    private String accessToken;
}
