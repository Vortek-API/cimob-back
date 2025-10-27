package fatec.vortek.cimob.presentation.controller;

import fatec.vortek.cimob.domain.service.RegistroVelocidadeService;
import fatec.vortek.cimob.presentation.dto.request.RegistroVelocidadeRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.RegistroVelocidadeResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/registros-velocidade")
@Tag(name = "Registros de velocidade")
public class RegistroVelocidadeController {

    private final RegistroVelocidadeService registroVelocidadeService;

    public RegistroVelocidadeController(RegistroVelocidadeService registroVelocidadeService) {
        this.registroVelocidadeService = registroVelocidadeService;
    }

    @PostMapping
    public ResponseEntity<RegistroVelocidadeResponseDTO> criar(@RequestBody RegistroVelocidadeRequestDTO dto) {
        RegistroVelocidadeResponseDTO novoRegistro = registroVelocidadeService.criar(dto);
        return new ResponseEntity<>(novoRegistro, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroVelocidadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(registroVelocidadeService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<RegistroVelocidadeResponseDTO>> listar() {
        return ResponseEntity.ok(registroVelocidadeService.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        registroVelocidadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}