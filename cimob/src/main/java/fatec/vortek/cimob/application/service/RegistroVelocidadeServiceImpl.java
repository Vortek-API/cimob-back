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
import fatec.vortek.cimob.presentation.dto.response.RegistroVelocidadeListagemResponseDTO;
import fatec.vortek.cimob.presentation.dto.response.RegistroVelocidadeResponseDTO;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistroVelocidadeServiceImpl implements RegistroVelocidadeService {

    private static final Logger log = LoggerFactory.getLogger(RegistroVelocidadeServiceImpl.class);

    private final RegistroVelocidadeRepository registroVelocidadeRepository;
    private final RadarRepository radarRepository;
    private final RegiaoRepository regiaoRepository;

    public RegistroVelocidadeServiceImpl(RegistroVelocidadeRepository registroVelocidadeRepository,
                                         RadarRepository radarRepository,
                                         RegiaoRepository regiaoRepository) {
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
    @Transactional(readOnly = true)
    public List<RegistroVelocidadeListagemResponseDTO> buscarPorFiltro(String radarId, Long regiaoId, boolean todasRegioes, LocalDateTime dataInicio, LocalDateTime dataFim) {
        String radarFiltro = radarId;
        boolean todasRegioesFiltro = todasRegioes;

        // Se vier "all" no radar, consideramos todas as regiões/radares
        if (radarFiltro != null && radarFiltro.equalsIgnoreCase("all")) {
            radarFiltro = null;
            todasRegioesFiltro = true;
        }

        log.info("Buscando registros por filtro - radarId: {}, regiaoId: {}, todasRegioes: {}, dataInicio: {}, dataFim: {}",
                radarFiltro, regiaoId, todasRegioesFiltro, dataInicio, dataFim);

        List<RegistroVelocidadeCache> cached;
        Long regiaoParaBusca = regiaoId;

        final String radarFiltroForError = radarFiltro;
        if (regiaoParaBusca == null && radarFiltro != null && !radarFiltro.isBlank()) {
            Radar radar = radarRepository.findById(radarFiltro)
                    .orElseThrow(() -> new RuntimeException("Radar não encontrado com ID: " + radarFiltroForError));
            regiaoParaBusca = radar.getRegiao() != null ? radar.getRegiao().getRegiaoId() : null;
        }

        if (todasRegioesFiltro) {
            List<Regiao> regioes = regiaoRepository.findAll();
            cached = new ArrayList<>();
            for (Regiao r : regioes) {
                if (r != null && !"S".equalsIgnoreCase(r.getDeletado())) {
                    cached.addAll(buscarRegistrosPorRegiao(r.getRegiaoId(), null));
                }
            }
        } else if (regiaoParaBusca != null) {
            cached = buscarRegistrosPorRegiao(regiaoParaBusca, null);
        } else {
            throw new RuntimeException("Região não informada e radarId não encontrado.");
        }

        final String radarFiltroFinal = radarFiltro;

        List<RegistroVelocidadeListagemResponseDTO> resposta = cached.stream()
                .filter(r -> radarFiltroFinal == null || radarFiltroFinal.isBlank() || radarFiltroFinal.equals(r.getRadarId()))
                .map(this::toListagemResponseDTO)
                .toList();

        log.info("Total de registros encontrados (cache): {}", resposta.size());
        return resposta;
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

    private RegistroVelocidadeListagemResponseDTO toListagemResponseDTO(RegistroVelocidadeCache registro) {
        Radar radar = null;
        if (registro.getRadarId() != null) {
            radar = radarRepository.findById(registro.getRadarId()).orElse(null);
        }
        String velocidadePermitida = radar != null && radar.getVelocidadePermitida() != null
                ? radar.getVelocidadePermitida().toString()
                : null;

        String dataFormatada = registro.getData() != null
                ? registro.getData().format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                : null;

        String regiaoId = registro.getRegiaoId() != null ? registro.getRegiaoId().toString() : null;

        return new RegistroVelocidadeListagemResponseDTO(
                registro.getRegistroVelocidadeId() != null ? registro.getRegistroVelocidadeId().toString() : null,
                registro.getRadarId(),
                regiaoId,
                registro.getTipoVeiculo() != null ? registro.getTipoVeiculo().getDescricao() : null,
                registro.getVelocidadeRegistrada() != null ? registro.getVelocidadeRegistrada().toString() : null,
                velocidadePermitida,
                dataFormatada,
                String.valueOf(registro.getDeletado())
        );
    }

    private RegistroVelocidadeListagemResponseDTO toListagemResponseDTO(RegistroVelocidade registro) {
        Radar radar = registro.getRadar();
        Regiao regiao = registro.getRegiao() != null ? registro.getRegiao() : (radar != null ? radar.getRegiao() : null);

        String regiaoId = regiao != null ? String.valueOf(regiao.getRegiaoId()) : null;
        String radarId = radar != null ? radar.getRadarId() : null;
        String velocidadePermitida = radar != null && radar.getVelocidadePermitida() != null
                ? radar.getVelocidadePermitida().toString()
                : null;

        String dataFormatada = registro.getData() != null
                ? registro.getData().format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                : null;

        return new RegistroVelocidadeListagemResponseDTO(
                registro.getRegistroVelocidadeId() != null ? registro.getRegistroVelocidadeId().toString() : null,
                radarId,
                regiaoId,
                registro.getTipoVeiculo() != null ? registro.getTipoVeiculo().getDescricao() : null,
                registro.getVelocidadeRegistrada() != null ? registro.getVelocidadeRegistrada().toString() : null,
                velocidadePermitida,
                dataFormatada,
                String.valueOf(registro.getDeletado())
        );
    }

    @Override
    @Cacheable(value = "buscarRegistrosPorRegiao", key = "{#regiaoId, #timestamp}")
    public List<RegistroVelocidadeCache> buscarRegistrosPorRegiao(Long regiaoId, String timestamp) {
        LocalDateTime inicioPeriodo;
        LocalDateTime fimPeriodo;

        if (timestamp != null && !timestamp.isEmpty()) {
            try {
                LocalDateTime timestampRef = LocalDateTime.parse(timestamp);
                inicioPeriodo = timestampRef.minusMinutes(AppConfig.getTimeWindowMinutes());
                fimPeriodo = timestampRef;
            } catch (DateTimeParseException ex) {
                try {
                    DateTimeFormatter fmtSemSegundos = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    LocalDateTime timestampRef = LocalDateTime.parse(timestamp, fmtSemSegundos);
                    inicioPeriodo = timestampRef.minusMinutes(AppConfig.getTimeWindowMinutes());
                    fimPeriodo = timestampRef;
                } catch (DateTimeParseException ex2) {
                    LocalDateTime agora = LocalDateTime.now();
                    inicioPeriodo = agora.minusMinutes(AppConfig.getTimeWindowMinutes());
                    fimPeriodo = agora;
                }
            }
        } else {
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

    // demais métodos mantêm cache/transactions existentes
}
