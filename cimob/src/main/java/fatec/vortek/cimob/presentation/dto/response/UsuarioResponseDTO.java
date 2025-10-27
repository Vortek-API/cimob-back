package fatec.vortek.cimob.presentation.dto.response;

import fatec.vortek.cimob.domain.enums.CargoUsuario;
import fatec.vortek.cimob.domain.model.Usuario;
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
    private Boolean deletado;

    public static UsuarioResponseDTO UsuarioModel2ResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .usuarioId(usuario.getUsuarioId())
                .nome(usuario.getNome())
                .cargo(usuario.getCargo())
                .cpf(usuario.getCpf())
                .email(usuario.getEmail())
                .deletado("S".equals((usuario.getDeletado())))
                .build();
    }
}
