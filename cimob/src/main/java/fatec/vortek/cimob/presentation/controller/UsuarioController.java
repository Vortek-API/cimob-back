package fatec.vortek.cimob.presentation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fatec.vortek.cimob.domain.enums.CargoUsuario;
import fatec.vortek.cimob.domain.service.UsuarioService;
import fatec.vortek.cimob.presentation.dto.request.UsuarioRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.UsuarioResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuarios")
public class UsuarioController {

    private final UsuarioService service; // <- usa a interface, não a implementação

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO usuario = service.criar(dto);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cargo/{email}")
    public ResponseEntity<CargoUsuario> getCargo(@PathVariable String email) {
        UsuarioResponseDTO usuario = service.buscarPorEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CargoUsuario.INDEFINIDO);
        }
        System.out.println("Cargo do usuário: " + usuario.getCargo());
        return ResponseEntity.ok(usuario.getCargo()); // Retorna apenas o cargo
    }

}
