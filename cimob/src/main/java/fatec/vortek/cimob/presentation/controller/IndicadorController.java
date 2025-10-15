package fatec.vortek.cimob.presentation.controller;

import fatec.vortek.cimob.application.service.IndicadorServiceImpl;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.presentation.dto.request.IndicadorRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.IndicadorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.tags.Tag;
import fatec.vortek.cimob.presentation.dto.response.IndiceCriticoResponseDTO;

@CrossOrigin
@RestController
@RequestMapping("/api/indicadores")
@RequiredArgsConstructor
@Tag(name = "Indicadores")
public class IndicadorController {

    private final IndicadorServiceImpl service;

    @PostMapping
    public ResponseEntity<IndicadorResponseDTO> criar(@RequestBody IndicadorRequestDTO dto) {
        Indicador i = Indicador.builder()
                .nome(dto.getNome())
                .valor(dto.getValor())
                .mnemonico(dto.getMnemonico())
                .descricao(dto.getDescricao())
                .deletado("N")
                .build();
        i = service.criar(i);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new IndicadorResponseDTO(i.getIndicadorId(), i.getNome(), i.getValor(), i.getMnemonico(), i.getDescricao(), null));
    }

    @GetMapping
    public ResponseEntity<List<IndicadorResponseDTO>> listar(
            @RequestParam(required = false) Long regiaoId,
            @RequestParam(required = false) String dataInicial) {
        List<Indicador> indicadores;
        
        if (regiaoId != null) {
            indicadores = service.listarPorRegiao(regiaoId, dataInicial);
        } else {
            indicadores = service.listarTodos(dataInicial).stream()
                    .filter(i -> !"S".equals(i.getDeletado()))
                    .collect(Collectors.toList());
        }
        
        List<IndicadorResponseDTO> list = indicadores.stream()
                .map(i -> new IndicadorResponseDTO(i.getIndicadorId(), i.getNome(), i.getValor(), i.getMnemonico(), i.getDescricao(), null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/indices-criticos")
    public ResponseEntity<List<IndiceCriticoResponseDTO>> listarIndicesCriticos(
            @RequestParam(required = false) Long regiaoId,
            @RequestParam(required = false) String dataInicial) {
        List<IndiceCriticoResponseDTO> resp = service.listarTopExcessosVelocidade(regiaoId, dataInicial);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{indicadorId}/associarEvento/{eventoId}")
    public ResponseEntity<Void> associarAEvento(@PathVariable Long indicadorId, @PathVariable Long eventoId) {
        service.associarAEvento(indicadorId, eventoId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{indicadorId}/desassociarEvento/{eventoId}")
    public ResponseEntity<Void> desassociarDeEvento(@PathVariable Long indicadorId, @PathVariable Long eventoId) {
        service.desassociarDeEvento(indicadorId, eventoId);
        return ResponseEntity.noContent().build();
    }
}
