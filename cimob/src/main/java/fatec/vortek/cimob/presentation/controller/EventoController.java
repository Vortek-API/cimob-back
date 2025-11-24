package fatec.vortek.cimob.presentation.controller;

import fatec.vortek.cimob.application.service.EventoServiceImpl;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.domain.model.Usuario;
import fatec.vortek.cimob.domain.service.UsuarioService;
import fatec.vortek.cimob.infrastructure.repository.RegiaoRepository;
import fatec.vortek.cimob.infrastructure.repository.UsuarioRepository;
import fatec.vortek.cimob.presentation.dto.request.EventoRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.EventoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoServiceImpl service;
    private final RegiaoRepository regiaoRepository;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar(@RequestBody EventoRequestDTO dto) {

        Evento e = Evento.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .build();

        e.setUsuario(usuarioService.getUsuarioLogado());

        if (dto.getRegioesIds() != null) {
            List<Regiao> regioes = regiaoRepository.findAllById(dto.getRegioesIds());
            e.setRegioes(regioes);
        }

        e = service.criar(e);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EventoResponseDTO.Model2ResponseDTO(e));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody EventoRequestDTO dto
    ) {
        Evento e = service.buscarPorId(id);

        e.setNome(dto.getNome());
        e.setDescricao(dto.getDescricao());
        e.setDataInicio(dto.getDataInicio());
        e.setDataFim(dto.getDataFim());

        if (dto.getRegioesIds() != null) {
            List<Regiao> regioes = regiaoRepository.findAllById(dto.getRegioesIds());
            e.setRegioes(regioes);
        }

        Evento atualizado = service.atualizar(e);

        return ResponseEntity.ok(EventoResponseDTO.Model2ResponseDTO(atualizado));
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listar() {
        List<EventoResponseDTO> lista = service.listarTodos().stream()
                .filter(e -> !"S".equals(e.getDeletado()))
                .map(EventoResponseDTO::Model2ResponseDTO)
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(@PathVariable Long id) {
        Evento e = service.buscarPorId(id);
        return ResponseEntity.ok(EventoResponseDTO.Model2ResponseDTO(e));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
