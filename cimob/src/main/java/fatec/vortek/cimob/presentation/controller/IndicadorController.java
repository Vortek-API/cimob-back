package fatec.vortek.cimob.presentation.controller;

import fatec.vortek.cimob.application.service.IndicadorServiceImpl;
import fatec.vortek.cimob.domain.enums.IndicadorMnemonico;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.presentation.dto.request.IndicadorRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.IndicadorResponseDTO;
import fatec.vortek.cimob.presentation.dto.response.IndicadorRadarResponseDTO;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.Response;
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
                .mnemonico(dto.getMnemonico())
                .descricao(dto.getDescricao())
                .deletado("N")
                .build();
        i = service.criar(i);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IndicadorResponseDTO.IndicadorModel2ResponseDTO(i));
    }

    @GetMapping
    public ResponseEntity<List<IndicadorResponseDTO>> listar(
            @RequestParam(required = false) Long regiaoId,
            @RequestParam(required = false) String timestamp) {
        List<Indicador> indicadores;
        
        if (regiaoId != null) {
            indicadores = service.listarPorRegiao(regiaoId, timestamp);
        } else {
            indicadores = service.listarTodos(timestamp).stream()
                    .filter(i -> !"S".equals(i.getDeletado()))
                    .collect(Collectors.toList());
        }
        
        List<IndicadorResponseDTO> list = indicadores.stream()
                .map(i -> IndicadorResponseDTO.IndicadorModel2ResponseDTO(i))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/atualiza-selecionados")
    public ResponseEntity<Void> fetchAtualizaSelecionados(@RequestBody List<Long> indicadoresId) {
        service.atualizaSelecionados(indicadoresId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/sem-calculo")
    public ResponseEntity<List<IndicadorResponseDTO>> listarSemCalculo() {
        List<Indicador> indicadores = service.listarIndicadoresSemCalculo();
        
        List<IndicadorResponseDTO> list = indicadores.stream()
                .map(i -> IndicadorResponseDTO.IndicadorModel2ResponseDTO(i))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/mnemonico/{mnemonico}")
    public ResponseEntity<IndicadorResponseDTO> obterPorMnemonico(
                                    @RequestParam IndicadorMnemonico mnemonico, 
                                    @RequestParam(required = false) Long regiaoId,
                                    @RequestParam(required = false) String timestamp) {

        Indicador indicador;
        if (regiaoId != null) {
            indicador = service.obterPorMnemonicoRegiao(mnemonico, regiaoId, timestamp);
        } else {
            indicador = service.obterPorMnemonico(mnemonico, timestamp);
        }

        return ResponseEntity.ok(IndicadorResponseDTO.IndicadorModel2ResponseDTO(indicador));
    }

    @GetMapping("/indices-criticos")
    public ResponseEntity<List<IndiceCriticoResponseDTO>> listarIndicesCriticos(
            @RequestParam(required = false) Long regiaoId,
            @RequestParam(required = false) String timestamp) {
        List<IndiceCriticoResponseDTO> resp = service.listarTopExcessosVelocidade(regiaoId, timestamp);
        return ResponseEntity.ok(resp);
    }
}
