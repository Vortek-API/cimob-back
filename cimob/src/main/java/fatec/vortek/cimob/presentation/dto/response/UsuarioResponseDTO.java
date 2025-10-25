package fatec.vortek.cimob.presentation.dto.response;

import fatec.vortek.cimob.domain.enums.CargoUsuario;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long usuarioId;
    private String nome;
    private CargoUsuario cargo;
    private String cpf;
    private String email;
    private String deletado;
}
