package fatec.vortek.cimob.presentation.controller;

import fatec.vortek.cimob.application.service.RegiaoServiceImpl;
import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.presentation.dto.request.RegiaoRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.RegiaoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/regioes")
@RequiredArgsConstructor
@Tag(name = "Regioes")
public class RegiaoController {

    private final RegiaoServiceImpl service;

    @PostMapping
    public ResponseEntity<RegiaoResponseDTO> criar(@RequestBody RegiaoRequestDTO dto) {
        Regiao regiao = Regiao.builder().nome(dto.getNome()).build();
        regiao = service.criar(regiao);
        RegiaoResponseDTO response = RegiaoResponseDTO.builder()
                .regiaoId(regiao.getRegiaoId())
                .nome(regiao.getNome())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RegiaoResponseDTO>> listar() {
        List<RegiaoResponseDTO> list = service.listarTodos().stream()
                .filter(r -> !"S".equals(r.getDeletado()))
                .map(r -> RegiaoResponseDTO.builder()
                        .regiaoId(r.getRegiaoId())
                        .nome(r.getNome())
                        .build())
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegiaoResponseDTO> buscar(@PathVariable Long id) {
        Regiao r = service.buscarPorId(id);
        if (r == null || "S".equals(r.getDeletado())) {
            return ResponseEntity.notFound().build();
        }
        RegiaoResponseDTO response = RegiaoResponseDTO.builder()
                .regiaoId(r.getRegiaoId())
                .nome(r.getNome())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegiaoResponseDTO> atualizar(@PathVariable Long id, @RequestBody RegiaoRequestDTO dto) {
        Regiao r = service.buscarPorId(id);
        if (r == null || "S".equals(r.getDeletado())) {
            return ResponseEntity.notFound().build();
        }
        r.setNome(dto.getNome());
        r = service.atualizar(r);
        RegiaoResponseDTO response = RegiaoResponseDTO.builder()
                .regiaoId(r.getRegiaoId())
                .nome(r.getNome())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Regiao r = service.buscarPorId(id);
        if (r == null || "S".equals(r.getDeletado())) {
            return ResponseEntity.notFound().build();
        }
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
