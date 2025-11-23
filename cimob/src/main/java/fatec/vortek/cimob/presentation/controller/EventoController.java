package fatec.vortek.cimob.presentation.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fatec.vortek.cimob.application.service.EventoServiceImpl;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.infrastructure.repository.RegiaoRepository;
import fatec.vortek.cimob.presentation.dto.request.EventoRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.EventoResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
@Tag(name = "Eventos")
public class EventoController {

    private final EventoServiceImpl service;
    private final RegiaoRepository regiaoRepository;

    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar(@RequestBody EventoRequestDTO dto) {
        Evento e = Evento.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .build();

        // Se vier regioesIds no DTO, carregar entidades e setar
        if (dto.getRegioesIds() != null && !dto.getRegioesIds().isEmpty()) {
            List<Regiao> regioes = regiaoRepository.findAllById(dto.getRegioesIds());
            e.setRegioes(regioes);
        }

        e = service.criar(e);
        EventoResponseDTO resp = new EventoResponseDTO(
                e.getEventoId(),
                e.getNome(),
                e.getDescricao(),
                e.getDataInicio(),
                e.getDataFim(),
                e.getUsuario() != null ? e.getUsuario().getUsuarioId() : null,
                e.getRegioes() != null ? e.getRegioes().stream().map(r -> r.getRegiaoId()).collect(Collectors.toList()) : null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(@PathVariable Long id, @RequestBody EventoRequestDTO dto) {
        Evento e = service.buscarPorId(id);
        e.setNome(dto.getNome());
        e.setDescricao(dto.getDescricao());

        if (dto.getRegioesIds() != null) {
            List<Regiao> regioes = regiaoRepository.findAllById(dto.getRegioesIds());
            e.setRegioes(regioes);
        } else {
            e.setRegioes(null);
        }

        Evento atualizado = service.atualizar(e);

        EventoResponseDTO resp = new EventoResponseDTO(
                atualizado.getEventoId(),
                atualizado.getNome(),
                atualizado.getDescricao(),
                atualizado.getDataInicio(),
                atualizado.getDataFim(),
                atualizado.getUsuario() != null ? atualizado.getUsuario().getUsuarioId() : null,
                atualizado.getRegioes() != null ? atualizado.getRegioes().stream().map(r -> r.getRegiaoId()).collect(Collectors.toList()) : null
        );

        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listar() {
        List<EventoResponseDTO> list = service.listarTodos().stream()
                .filter(e -> !"S".equals(e.getDeletado()))
                .map(e -> EventoResponseDTO.Model2ResponseDTO(e))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(@PathVariable Long id) {
        Evento e = service.buscarPorId(id);
        EventoResponseDTO resp = new EventoResponseDTO(
                e.getEventoId(),
                e.getNome(),
                e.getDescricao(),
                e.getDataInicio(),
                e.getDataFim(),
                e.getUsuario() != null ? e.getUsuario().getUsuarioId() : null,
                e.getRegioes() != null ? e.getRegioes().stream().map(r -> r.getRegiaoId()).collect(Collectors.toList()) : null
        );
        return ResponseEntity.ok(resp);
    }
}
