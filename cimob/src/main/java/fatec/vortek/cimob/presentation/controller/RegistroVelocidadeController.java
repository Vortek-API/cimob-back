package fatec.vortek.cimob.presentation.controller;

import fatec.vortek.cimob.domain.service.RegistroVelocidadeService;
import fatec.vortek.cimob.presentation.dto.request.RegistroVelocidadeRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.RegistroVelocidadeResponseDTO;
import fatec.vortek.cimob.presentation.dto.response.RegistroVelocidadeListagemResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/registros-velocidade")
@Tag(name = "Registros de velocidade")
public class RegistroVelocidadeController {

    private static final Logger log = LoggerFactory.getLogger(RegistroVelocidadeController.class);

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

    @GetMapping("/filtro")
    public ResponseEntity<List<RegistroVelocidadeListagemResponseDTO>> listarPorFiltro(@RequestParam(required = false) String radarId,
                                                                                      @RequestParam(required = false) Long regiaoId,
                                                                                      @RequestParam(defaultValue = "false") boolean todasRegioes,
                                                                                      @RequestParam(required = false) String dataInicio,
                                                                                      @RequestParam(required = false) String dataFim) {
        log.info("Filtro recebido - radarId: {}, regiaoId: {}, todasRegioes: {}, dataInicio: {}, dataFim: {}",
                radarId, regiaoId, todasRegioes, dataInicio, dataFim);

        if ((radarId == null || radarId.isBlank()) && !todasRegioes && regiaoId == null) {
            log.warn("Filtro inválido: radarId/regiaoId/todasRegioes ausentes.");
            return ResponseEntity.badRequest().build();
        }

        if (radarId != null && !radarId.isBlank() && (todasRegioes || regiaoId != null)) {
            log.warn("Filtro inválido: radarId informado junto com regiaoId ou todasRegioes=true.");
            return ResponseEntity.badRequest().build();
        }

        LocalDate inicio = null;
        LocalDate fim = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        if (dataInicio != null || dataFim != null) {
            if (dataInicio == null || dataFim == null) {
                return ResponseEntity.badRequest().build();
            }
            try {
                inicio = LocalDate.parse(dataInicio, formatter);
                fim = LocalDate.parse(dataFim, formatter);
            } catch (DateTimeParseException ex) {
                return ResponseEntity.badRequest().build();
            }
        }

        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            return ResponseEntity.badRequest().build();
        }

        LocalDateTime inicioPeriodo = inicio != null ? inicio.atStartOfDay() : null;
        LocalDateTime fimPeriodo = fim != null ? fim.atTime(LocalTime.MAX) : null;

        List<RegistroVelocidadeListagemResponseDTO> resposta = registroVelocidadeService.buscarPorFiltro(
                radarId,
                regiaoId,
                todasRegioes,
                inicioPeriodo,
                fimPeriodo);

        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        registroVelocidadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
