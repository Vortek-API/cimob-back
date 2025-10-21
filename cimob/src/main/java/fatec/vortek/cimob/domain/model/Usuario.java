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
    @Column(name = "USUARIOID")
    private Long usuarioId;

    @Column(name = "CPF", nullable = false, length = 11)
    private String cpf;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "EMAIL", nullable = false, length = 150)
    private String email;

    @Column(name = "CARGO", nullable = false, length = 100)
    private String cargo;

    @Column(name = "DELETADO", nullable = false, length = 1)
    private String deletado = "N";

    @Column(name = "SENHA", nullable = false, length = 255)
    private String senha; 

    @Column(name = "REFRESH_TOKEN") 
    private String refreshToken;

    @Column(name = "ACCESS_TOKEN")
    private String accessToken;
}
