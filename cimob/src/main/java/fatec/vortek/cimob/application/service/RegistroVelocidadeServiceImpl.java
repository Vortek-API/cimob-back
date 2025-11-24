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
        log.info("Buscando registros por filtro - radarId: {}, regiaoId: {}, todasRegioes: {}, dataInicio: {}, dataFim: {}",
                radarId, regiaoId, todasRegioes, dataInicio, dataFim);

        List<RegistroVelocidadeCache> cached;
        Long regiaoParaBusca = regiaoId;

        if (regiaoParaBusca == null && radarId != null && !radarId.isBlank()) {
            Radar radar = radarRepository.findById(radarId)
                    .orElseThrow(() -> new RuntimeException("Radar não encontrado com ID: " + radarId));
            regiaoParaBusca = radar.getRegiao() != null ? radar.getRegiao().getRegiaoId() : null;
        }

        if (regiaoParaBusca != null) {
            cached = buscarRegistrosPorRegiao(regiaoParaBusca, null);
        } else if (todasRegioes) {
            cached = buscarRegistrosPorRegiao(null, null);
        } else {
            throw new RuntimeException("Região não informada e radarId não encontrado.");
        }

        List<RegistroVelocidadeListagemResponseDTO> resposta = cached.stream()
                .filter(r -> radarId == null || radarId.isBlank() || radarId.equals(r.getRadarId()))
                .filter(r -> {
                    if (dataInicio == null && dataFim == null) return true;
                    if (r.getData() == null) return false;
                    boolean afterStart = dataInicio == null || !r.getData().isBefore(dataInicio);
                    boolean beforeEnd = dataFim == null || !r.getData().isAfter(dataFim);
                    return afterStart && beforeEnd;
                })
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
    @Cacheable(value = "buscarRegistrosPorRegiao", key = "#regiaoId != null ? #regiaoId : 'ALL'")
    public List<RegistroVelocidadeCache> buscarRegistrosPorRegiao(Long regiaoId, String timestamp) {
        List<RegistroVelocidade> resultado;
        if (regiaoId == null) {
            resultado = registroVelocidadeRepository.findByFiltros(null, null, true, null, null);
        } else {
            resultado = registroVelocidadeRepository.findByFiltros(null, regiaoId, false, null, null);
        }
        return resultado.stream()
                .map(RegistroVelocidadeCache::Model2ModelCache)
                .collect(Collectors.toList());
    }

    // demais métodos mantêm cache/transactions existentes
}
