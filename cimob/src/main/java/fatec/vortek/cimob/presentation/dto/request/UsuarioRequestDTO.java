package fatec.vortek.cimob.presentation.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequestDTO {
    private String cpf;
    private String nome;
    private String email;
    private String cargo;
    private String senha; 

    @Builder.Default
    private String deletado = "N";
}
