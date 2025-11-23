package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.model.Radar;
import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.domain.model.RegistroVelocidade;
import fatec.vortek.cimob.domain.model.RegistroVelocidadeCache;
import fatec.vortek.cimob.domain.service.RegistroVelocidadeService;
import fatec.vortek.cimob.infrastructure.config.AppConfig;
import fatec.vortek.cimob.infrastructure.repository.RadarRepository;
import fatec.vortek.cimob.infrastructure.repository.RegiaoRepository;
import fatec.vortek.cimob.infrastructure.repository.RegistroVelocidadeRepository;
import fatec.vortek.cimob.presentation.dto.request.RegistroVelocidadeRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.RegistroVelocidadeResponseDTO;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistroVelocidadeServiceImpl implements RegistroVelocidadeService {

    private final RegistroVelocidadeRepository registroVelocidadeRepository;
    private final RadarRepository radarRepository;
    private final RegiaoRepository regiaoRepository;

    public RegistroVelocidadeServiceImpl(RegistroVelocidadeRepository registroVelocidadeRepository, RadarRepository radarRepository, RegiaoRepository regiaoRepository) {
        this.registroVelocidadeRepository = registroVelocidadeRepository;
        this.radarRepository = radarRepository;
        this.regiaoRepository = regiaoRepository;
    }

    @Override
    @Transactional
    public RegistroVelocidadeResponseDTO criar(RegistroVelocidadeRequestDTO dto) {
        Radar radar = radarRepository.findById(dto.getRadarId())
                .orElseThrow(() -> new RuntimeException("Radar não encontrado com ID: " + dto.getRadarId()));
        Regiao regiao = regiaoRepository.findById(dto.getRegiaoId())
                .orElseThrow(() -> new RuntimeException("Radar não encontrado com ID: " + dto.getRadarId()));

        RegistroVelocidade novoRegistro = new RegistroVelocidade();
        novoRegistro.setRadar(radar);
        novoRegistro.setRegiao(regiao);
        novoRegistro.setTipoVeiculo(dto.getTipoVeiculo());
        novoRegistro.setVelocidadeRegistrada(dto.getVelocidadeRegistrada());
        novoRegistro.setData(dto.getData());

        RegistroVelocidade registroSalvo = registroVelocidadeRepository.save(novoRegistro);
        return toResponseDTO(registroSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroVelocidadeResponseDTO buscarPorId(Long id) {
        RegistroVelocidade registro = registroVelocidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de Velocidade não encontrado com ID: " + id));
        return toResponseDTO(registro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroVelocidadeResponseDTO> listarTodos() {
        return registroVelocidadeRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!registroVelocidadeRepository.existsById(id)) {
            throw new RuntimeException("Registro de Velocidade não encontrado com ID: " + id);
        }
        registroVelocidadeRepository.deleteById(id);
    }

    private RegistroVelocidadeResponseDTO toResponseDTO(RegistroVelocidade registro) {
        return RegistroVelocidadeResponseDTO.RegistroVelocidadeModel2ResponseDTO(registro);
    }

    @Override
    @Cacheable(
    value = "buscarRegistrosPorRegiao",
    key = "{#regiaoId, #timestamp}"
    )
    public List<RegistroVelocidadeCache> buscarRegistrosPorRegiao(Long regiaoId, String timestamp) {
        LocalDateTime inicioPeriodo;
        LocalDateTime fimPeriodo;

        if (timestamp != null && !timestamp.isEmpty()) {
            try {
                // Se timestamp fornecido, usar ele como ponto de referência
                // Frontend agora envia horário local sem timezone
                LocalDateTime timestampRef = LocalDateTime.parse(timestamp);

                inicioPeriodo = timestampRef.minusMinutes(AppConfig.getTimeWindowMinutes());
                fimPeriodo = timestampRef;
            } catch (DateTimeParseException ex) {
                try {
                    // Fallback para formato sem segundos
                    DateTimeFormatter fmtSemSegundos = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    LocalDateTime timestampRef = LocalDateTime.parse(timestamp, fmtSemSegundos);
                    inicioPeriodo = timestampRef.minusMinutes(AppConfig.getTimeWindowMinutes());
                    fimPeriodo = timestampRef;
                } catch (DateTimeParseException ex2) {
                    // Se ainda falhar, usar comportamento padrão
                    LocalDateTime agora = LocalDateTime.now();
                    inicioPeriodo = agora.minusMinutes(AppConfig.getTimeWindowMinutes());
                    fimPeriodo = agora;
                }
            }
        } else {
            // Comportamento padrão: últimos X minutos (AppConfig)
            LocalDateTime agora = LocalDateTime.now();
            inicioPeriodo = agora.minusMinutes(AppConfig.getTimeWindowMinutes());
            fimPeriodo = agora;
        }
        List<RegistroVelocidade> resultado;
        if (regiaoId == null) {
            resultado = registroVelocidadeRepository.findByDataBetweenAndDeletado(inicioPeriodo, fimPeriodo);
        } else {
            resultado = registroVelocidadeRepository.findByDataBetweenAndRegiaoAndDeletado(inicioPeriodo, fimPeriodo, regiaoId);
        }
        return resultado.stream()
                .map(RegistroVelocidadeCache::Model2ModelCache)
                .collect(Collectors.toList());
    }
}