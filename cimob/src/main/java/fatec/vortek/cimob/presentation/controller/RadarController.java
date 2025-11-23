package fatec.vortek.cimob.presentation.controller;

import fatec.vortek.cimob.application.service.RadarServiceImpl;
import fatec.vortek.cimob.application.service.RegiaoServiceImpl;
import fatec.vortek.cimob.domain.model.Radar;
import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.presentation.dto.request.RadarRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.RadarResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/radares")
@RequiredArgsConstructor
@Tag(name = "Radares")
public class RadarController {

    private final RadarServiceImpl radarService;
    private final RegiaoServiceImpl regiaoService;

    @PostMapping
    public ResponseEntity<RadarResponseDTO> criar(@RequestBody RadarRequestDTO dto) {
        Regiao regiao = regiaoService.buscarPorId(dto.getRegiaoId());
        if (regiao == null || "S".equals(regiao.getDeletado())) {
            return ResponseEntity.badRequest().build();
        }
        Radar radar = Radar.builder()
                .regiao(regiao)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .endereco(dto.getEndereco())
                .velocidadePermitida(dto.getVelocidadePermitida())
                .build();
        radar = radarService.criar(radar);
        RadarResponseDTO response = RadarResponseDTO.builder()
                .radarId(radar.getRadarId())
                .regiaoId(regiao.getRegiaoId())
                .latitude(radar.getLatitude())
                .longitude(radar.getLongitude())
                .endereco(radar.getEndereco())
                .velocidadePermitida(radar.getVelocidadePermitida())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RadarResponseDTO>> listar() {
        List<RadarResponseDTO> list = radarService.listarTodos().stream()
                .filter(r -> !"S".equals(r.getDeletado()))
                .map(r -> RadarResponseDTO.builder()
                        .radarId(r.getRadarId())
                        .regiaoId(r.getRegiao().getRegiaoId())
                        .latitude(r.getLatitude())
                        .longitude(r.getLongitude())
                        .endereco(r.getEndereco())
                        .velocidadePermitida(r.getVelocidadePermitida())
                        .build())
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RadarResponseDTO> buscar(@PathVariable String id) {
        Radar r = radarService.buscarPorId(id);
        if (r == null || "S".equals(r.getDeletado())) {
            return ResponseEntity.notFound().build();
        }
        RadarResponseDTO response = RadarResponseDTO.builder()
                .radarId(r.getRadarId())
                .regiaoId(r.getRegiao().getRegiaoId())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .endereco(r.getEndereco())
                .velocidadePermitida(r.getVelocidadePermitida())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RadarResponseDTO> atualizar(@PathVariable String id, @RequestBody RadarRequestDTO dto) {
        Radar r = radarService.buscarPorId(id);
        if (r == null || "S".equals(r.getDeletado())) {
            return ResponseEntity.notFound().build();
        }
        Regiao regiao = regiaoService.buscarPorId(dto.getRegiaoId());
        if (regiao == null || "S".equals(regiao.getDeletado())) {
            return ResponseEntity.badRequest().build();
        }
        r.setRegiao(regiao);
        r.setLatitude(dto.getLatitude());
        r.setLongitude(dto.getLongitude());
        r.setEndereco(dto.getEndereco());
        r.setVelocidadePermitida(dto.getVelocidadePermitida());
        r = radarService.atualizar(r);
        RadarResponseDTO response = RadarResponseDTO.builder()
                .radarId(r.getRadarId())
                .regiaoId(regiao.getRegiaoId())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .endereco(r.getEndereco())
                .velocidadePermitida(r.getVelocidadePermitida())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        Radar r = radarService.buscarPorId(id);
        if (r == null || "S".equals(r.getDeletado())) {
            return ResponseEntity.notFound().build();
        }
        radarService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
