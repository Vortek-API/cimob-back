package fatec.vortek.cimob.presentation.controller;

import fatec.vortek.cimob.application.service.EventoServiceImpl;
import fatec.vortek.cimob.application.service.TimelineServiceImpl;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.presentation.dto.request.EventoRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.EventoResponseDTO;
import fatec.vortek.cimob.presentation.dto.response.TimelineResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin
@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
@Tag(name = "Timeline")
public class TimelineController {

    private final TimelineServiceImpl service;
    
    @GetMapping
    public ResponseEntity<List<TimelineResponseDTO>> listar() {
        List<TimelineResponseDTO> list = service.listarTodos().stream()
                .map(TimelineResponseDTO::TimelineModel2ResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
