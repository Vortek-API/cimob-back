package fatec.vortek.cimob.presentation.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long usuarioId;
    private String nome;
    private String cargo;
    private String cpf;
    private String email;
    private String sobreNome;
    private String deletado;
    private String userName;
}
