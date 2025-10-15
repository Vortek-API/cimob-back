package fatec.vortek.cimob.presentation.controller;

import fatec.vortek.cimob.application.service.EventoServiceImpl;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.presentation.dto.request.EventoRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.EventoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
@Tag(name = "Eventos")
public class EventoController {

    private final EventoServiceImpl service;

    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar(@RequestBody EventoRequestDTO dto) {
        Evento e = Evento.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .build();
        e = service.criar(e);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new EventoResponseDTO(e.getEventoId(), e.getNome(), e.getDescricao(), e.getData(), null, null, null));
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listar() {
        List<EventoResponseDTO> list = service.listarTodos().stream()
                .filter(e -> !"S".equals(e.getDeletado()))
                .map(e -> new EventoResponseDTO(e.getEventoId(), e.getNome(), e.getDescricao(), e.getData(), null, null, null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{eventoId}/associarIndicador/{indicadorId}")
    public ResponseEntity<Void> associarIndicador(@PathVariable Long eventoId, @PathVariable Long indicadorId) {
        service.associarIndicador(eventoId, indicadorId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{eventoId}/desassociarIndicador/{indicadorId}")
    public ResponseEntity<Void> desassociarIndicador(@PathVariable Long eventoId, @PathVariable Long indicadorId) {
        service.desassociarIndicador(eventoId, indicadorId);
        return ResponseEntity.noContent().build();
    }
}
