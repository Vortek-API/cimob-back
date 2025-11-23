package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.enums.IndicadorMnemonico;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.domain.model.Radar;
import fatec.vortek.cimob.domain.model.RegistroVelocidade;
import fatec.vortek.cimob.domain.model.RegistroVelocidadeCache;
import fatec.vortek.cimob.domain.service.IndicadorService;
import fatec.vortek.cimob.domain.service.RadarService;
import fatec.vortek.cimob.domain.service.RegiaoService;
import fatec.vortek.cimob.domain.service.RegistroVelocidadeService;
import fatec.vortek.cimob.infrastructure.repository.IndicadorRepository;
import fatec.vortek.cimob.infrastructure.repository.RadarRepository;
import fatec.vortek.cimob.infrastructure.repository.RegiaoRepository;
import fatec.vortek.cimob.infrastructure.repository.EventoRepository;
import fatec.vortek.cimob.infrastructure.repository.RegistroVelocidadeRepository;
import fatec.vortek.cimob.infrastructure.config.AppConfig;
import fatec.vortek.cimob.presentation.dto.response.IndiceCriticoResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import fatec.vortek.cimob.domain.model.RegistroVelocidade;
import fatec.vortek.cimob.domain.enums.TipoVeiculo;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndicadorServiceImpl implements IndicadorService {

    private final IndicadorRepository repository;
    private final EventoRepository eventoRepository;
    private final RadarRepository radarRepository;
    private final RegiaoService regiaoService;
    private final RegiaoRepository regiaoRepository;
    private final RegistroVelocidadeRepository registroVelocidadeRepository;
    private final TimelineServiceImpl timelineService;
    private final RegistroVelocidadeService registroVelocidadeService;
    
    // private record PontoHoraKey(String radarId, int hora) {
    // }

    @Override
    public Indicador criar(Indicador indicador) {
        return repository.save(indicador);
    }

    @Override
    public Indicador atualizar(Indicador indicador) {
        return repository.save(indicador);
    }

    @Override
    public void deletar(Long id) {
        Indicador i = repository.findById(id).orElseThrow();
        i.setDeletado("S");
        repository.save(i);
    }

    @Override
    public Indicador buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Indicador> listarTodos() {
        return listarTodos(null);
    }

    @Override
    public List<Indicador> listarIndicadoresSemCalculo() {
        return repository.findAll().stream()
                .filter(ind -> !"S".equals(ind.getDeletado()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void atualizaSelecionados(List<Long> indicadoresId) {
        List<Indicador> todosIndicadores = repository.findAll();

        repository.updateOcultoByIdsCase(indicadoresId);

        for (Indicador indicador : todosIndicadores) {
            boolean estaSelecionado = indicadoresId.contains(indicador.getIndicadorId());
            if (estaSelecionado && "S".equals(indicador.getOculto())) {
                timelineService.criarTimelineIndicadorDesoculto(indicador);
            } else if (!estaSelecionado && "N".equals(indicador.getOculto())) {
                timelineService.criarTimelineIndicadorOculto(indicador);
            }
        }
    }

    @Override
    public List<Indicador> listarTodos(String timestamp) {
        return listarTodos(timestamp, null);
    }

    @Override
    public List<Indicador> listarTodos(String timestamp, String radarId) {

        List<Indicador> indicadores = repository.findAll().stream()
                .filter(ind -> !"S".equals(ind.getDeletado()))
                .filter(ind -> !"S".equals(ind.getOculto()))
                .toList();

        List<RegistroVelocidade> registros = buscarRegistrosPorRegiao(null, timestamp);

        List<RegistroVelocidade> registrosFiltrados =
                (radarId != null && !radarId.isBlank())
                ? registros.stream()
                    .filter(r -> r.getRadarId() != null &&
                                radarId.equals(r.getRadarId()))
                    .toList()
                : registros;

        for (Indicador indicador : indicadores) {
            calcularValorIndicadoresComRegistros(indicador, registrosFiltrados);
        }

        return indicadores;
    }

    @Override
    public List<Indicador> listarPorRegiao(Long regiaoId) {
        return listarPorRegiao(regiaoId, null);
    }

    public List<Indicador> listarPorRegiao(Long regiaoId, String timestamp) {
        return listarPorRegiao(regiaoId, timestamp, null);
    }

    @Override
    public List<Indicador> listarPorRegiao(Long regiaoId, String timestamp, String radarId) {

        Regiao regiao = regiaoService.buscarPorId(regiaoId);
        if (regiao == null) {
            throw new RuntimeException("Região não encontrada com ID: " + regiaoId);
        }

        List<Indicador> indicadores = repository.findAll().stream()
                .filter(ind -> !"S".equals(ind.getDeletado()))
                .filter(ind -> !"S".equals(ind.getOculto()))
                .toList();

        List<RegistroVelocidade> registros = buscarRegistrosPorRegiao(regiaoId, timestamp);

        List<RegistroVelocidade> registrosFiltrados =
                (radarId != null && !radarId.isBlank())
                        ? registros.stream()
                            .filter(r -> r.getRadarId() != null &&
                                        radarId.equals(r.getRadarId()))
                            .toList()
                        : registros;

        for (Indicador indicador : indicadores) {
            calcularValorIndicadoresComRegistros(indicador, registrosFiltrados);
        }

        return indicadores;
    }

    @Override
    public Indicador obterPorMnemonicoRegiao(IndicadorMnemonico mnemonico, Long regiaoId, String timestamp) {
        Regiao regiao = regiaoService.buscarPorId(regiaoId);
        if (regiao == null) {
            throw new RuntimeException("Região não encontrada com ID: " + regiaoId);
        }

        Indicador indicador = repository.findByMnemonico(mnemonico);

        List<RegistroVelocidade> registros = buscarRegistrosPorRegiao(regiaoId, timestamp);

        indicador = calcularValorIndicadoresComRegistros(indicador, registros);

        return indicador;
    }

    @Override
    public Indicador obterPorMnemonico(IndicadorMnemonico mnemonico, String timestamp) {
        Indicador indicador = repository.findByMnemonico(mnemonico);

        List<RegistroVelocidade> registros = buscarRegistrosPorRegiao(null, timestamp);

        indicador = calcularValorIndicadoresComRegistros(indicador, registros);

        return indicador;
    }
    @Override
    public java.util.List<IndiceCriticoResponseDTO> listarTopExcessosVelocidade(Long regiaoId) {
        return listarTopExcessosVelocidade(regiaoId, null);
    }

    @Override
    public java.util.List<IndiceCriticoResponseDTO> listarTopExcessosVelocidade(Long regiaoId, String timestamp) {
        List<RegistroVelocidade> registros = buscarRegistrosPorRegiao(regiaoId, timestamp);

        class Agg { double somaVel; int cont; LocalDateTime minInf; LocalDateTime maxInf; Integer velPerm; Long regiaoId; String regiaoNome; String endereco; }

        java.util.Map<String, Agg> mapa = new java.util.HashMap<>();

        registros.stream()
                .filter(r -> r.getVelocidadeRegistrada() != null && r.getRadar() != null
                        && r.getRadar().getVelocidadePermitida() != null && r.getData() != null)
                .forEach(r -> {
                    Radar rad = r.getRadar();
                    String endereco = rad.getEndereco() != null ? rad.getEndereco() : "";
                    Integer velPerm = rad.getVelocidadePermitida();
                    Long regIdVal = rad.getRegiao() != null ? rad.getRegiao().getRegiaoId() : null;
                    String regNomeVal = rad.getRegiao() != null ? rad.getRegiao().getNome() : null;
                    String key = endereco + "|" + velPerm + "|" + regIdVal + "|" + regNomeVal;
                    Agg agg = mapa.computeIfAbsent(key, k -> {
                        Agg a = new Agg();
                        a.velPerm = velPerm;
                        a.regiaoId = regIdVal;
                        a.regiaoNome = regNomeVal;
                        a.endereco = endereco;
                        return a;
                    });
                    if (r.getVelocidadeRegistrada() > velPerm) {
                        agg.somaVel += r.getVelocidadeRegistrada();
                        agg.cont += 1;
                        LocalDateTime dt = r.getData();
                        if (agg.minInf == null || dt.isBefore(agg.minInf))
                            agg.minInf = dt;
                        if (agg.maxInf == null || dt.isAfter(agg.maxInf))
                            agg.maxInf = dt;
                    }
                });

        DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern("HH'h'mm");

        return mapa.values().stream()
    .filter(a -> a.cont > 0)
    .map(a -> {
        double media = a.somaVel / a.cont;
        double excessoMedio = media - a.velPerm;
        String intervalo = null;
        if (a.minInf != null && a.maxInf != null) {
            if (a.minInf.equals(a.maxInf)) {
                intervalo = "às " + a.minInf.format(horaFmt);
            } else {
                intervalo = "das " + a.minInf.format(horaFmt) + " às " + a.maxInf.format(horaFmt);
            }
        }
        return new Object[]{a.endereco, a.velPerm, media, a.regiaoId, a.regiaoNome, excessoMedio, intervalo};
    })
    .filter(arr -> ((Double) arr[5]) > 0)
    .sorted(Comparator.comparingDouble(o -> -((Double) ((Object[]) o)[5])))
    .limit(3)
    .map(arr -> IndiceCriticoResponseDTO.builder()
            .endereco((String) arr[0])
            .velocidadePermitida((Integer) arr[1])
            .velocidadeRegistrada((int) Math.round((Double) arr[2]))
            .regiaoId((Long) arr[3])
            .regiaoNome((String) arr[4])
            .dataHora((String) arr[6])
            .build())
    .collect(Collectors.toList());
    }

    public Indicador calcularValorIndicadores(Indicador indicador) {
        return calcularValorIndicadores(indicador, null);
    }

    public Indicador calcularValorIndicadores(Indicador indicador, String timestamp) {
        return calcularValorIndicadoresPorRegiao(indicador, null, timestamp);
    }

    public Indicador calcularValorIndicadoresPorRegiao(Indicador indicador, Long regiaoId) {
        return calcularValorIndicadoresPorRegiao(indicador, regiaoId, null);
    }

    public Indicador calcularValorIndicadoresPorRegiao(Indicador indicador, Long regiaoId, String timestamp) {
        List<RegistroVelocidade> registros = buscarRegistrosPorRegiao(regiaoId, timestamp);

        return calcularValorIndicadoresComRegistros(indicador, registros);
    }

    private Indicador calcularValorIndicadoresComRegistros(Indicador indicador, List<RegistroVelocidade> registros) {
        if (indicador.getMnemonico() == IndicadorMnemonico.EXCESSO_VELOCIDADE) {
            indicador.setValor(calcularExcessoVelocidadeComRegistros(registros));
        } else if (indicador.getMnemonico() == IndicadorMnemonico.VARIABILIDADE_VELOCIDADE) {
            indicador.setValor(calcularVariabilidadeVelocidadeComRegistros(registros));
        } else if (indicador.getMnemonico() == IndicadorMnemonico.VEICULOS_LENTOS) {
            indicador.setValor(calcularVeiculosLentosComRegistros(registros));
        } else if (indicador.getMnemonico() == IndicadorMnemonico.FLUXO_VEICULOS) {
            indicador.setValor(calcularFluxoVeiculosComRegistros(registros));
        } else if (indicador.getMnemonico() == IndicadorMnemonico.DIFERENCA_MEDIA_VELOCIDADE) {
            indicador.setValor(calcularDiferencaMediaVelocidadeComRegistros(registros)); 
        } else if (indicador.getMnemonico() == IndicadorMnemonico.HOMOGENEIDADE_VELOCIDADE) {
            indicador.setValor(calcularCoeficienteHomogeneidadeVelocidade(registros));
        } else if (indicador.getMnemonico() == IndicadorMnemonico.FREQUENCIA_PICOS_HORA) {
            indicador.setValor(calcularFrequenciaPicosPorHora(registros)); 
        } else {
            indicador.setValor(0.0);
        }
        return indicador;
    }

    private Double calcularExcessoVelocidadeComRegistros(List<RegistroVelocidade> registros) {
        List<RegistroVelocidade> regs = registros.stream()
                .filter(r -> r.getVelocidadeRegistrada() != null && r.getRadar() != null && r.getRadar().getVelocidadePermitida() != null)
                .collect(Collectors.toList());

        if (regs.isEmpty()) {
            return 1.0;
        }

        long totalRegistros = regs.size();
        long excessos = regs.stream()
                .filter(registro -> registro.getVelocidadeRegistrada() > registro.getRadar().getVelocidadePermitida())
                .count();

        double percentualExcessos = (double) excessos / totalRegistros * 100;

        if (percentualExcessos <= 10) {
            return 1.0;
        } else if (percentualExcessos <= 25) {
            return 2.0;
        } else {
            return 3.0;
        }
    }

    private Double calcularVariabilidadeVelocidadeComRegistros(List<RegistroVelocidade> registros) {
        List<Integer> velocidades = registros.stream()
                .map(RegistroVelocidade::getVelocidadeRegistrada)
                .filter(v -> v != null)
                .collect(Collectors.toList());

        if (velocidades.size() < 2) {
            return 1.0;
        }

        double media = velocidades.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double variancia = velocidades.stream()
                .mapToDouble(v -> Math.pow(v - media, 2))
                .average().orElse(0.0);

        double desvioPadrao = Math.sqrt(variancia);

        if (desvioPadrao <= 5) {
            return 1.0;
        } else if (desvioPadrao <= 15) {
            return 2.0;
        } else {
            return 3.0;
        }
    }

    private Double calcularVeiculosLentosComRegistros(List<RegistroVelocidade> registros) {
        List<RegistroVelocidade> regs = registros.stream()
                .filter(r -> r.getVelocidadeRegistrada() != null && r.getRadar() != null && r.getRadar().getVelocidadePermitida() != null)
                .collect(Collectors.toList());

        if (regs.isEmpty()) {
            return 1.0;
        }

        long totalRegistros = regs.size();
        long veiculosLentos = regs.stream()
                .filter(registro -> {
                    int velocidadePermitida = registro.getRadar().getVelocidadePermitida();
                    int velocidadeRegistrada = registro.getVelocidadeRegistrada();
                    return velocidadeRegistrada < (velocidadePermitida * 0.5);
                })
                .count();

        double percentualVeiculosLentos = (double) veiculosLentos / totalRegistros * 100;

        if (percentualVeiculosLentos <= 5) {
            return 1.0;
        } else if (percentualVeiculosLentos <= 15) {
            return 2.0;
        } else {
            return 3.0;
        }
    }

    private Double calcularFluxoVeiculosComRegistros(List<RegistroVelocidade> registros) {
        if (registros == null || registros.isEmpty()) {
            return 1.0;
        }

        int total = registros.size();

        if (total <= 100) {
            return 1.0;
        } else if (total <= 500) {
            return 2.0;
        } else {
            return 3.0;
        }
    }

    private Double calcularDiferencaMediaVelocidadeComRegistros(List<RegistroVelocidade> registros) {
        List<Double> diferencas = registros.stream()
                .filter(r -> r.getVelocidadeRegistrada() != null &&
                        r.getRadar() != null &&
                        r.getRadar().getVelocidadePermitida() != null)
                .map(r -> (double) r.getVelocidadeRegistrada() - r.getRadar().getVelocidadePermitida())
                .toList();

        if (diferencas.isEmpty()) {
            return 1.0;
        }

        double mediaDiferenca = diferencas.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        if (mediaDiferenca <= 0) {
            return 1.0;
        } else if (mediaDiferenca <= 10) {
            return 2.0;
        } else {
            return 3.0;
        }
    }

    private Double calcularCoeficienteHomogeneidadeVelocidade(List<RegistroVelocidade> registros) {
        List<RegistroVelocidade> regs = registros.stream()
                .filter(r -> r.getVelocidadeRegistrada() != null)
                .sorted(Comparator.comparing(RegistroVelocidade::getData))
                .toList();

        if (regs.size() < 2) {
            return 1.0;
        }

        List<Integer> diferencas = new ArrayList<>();
        for (int i = 1; i < regs.size(); i++) {
            int diff = Math.abs(regs.get(i).getVelocidadeRegistrada() - regs.get(i - 1).getVelocidadeRegistrada());
            diferencas.add(diff);
        }

        double mediaDiferencas = diferencas.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        if (mediaDiferencas <= 5) {
            return 1.0; // homogêneo
        } else if (mediaDiferencas <= 15) {
            return 2.0; // médio
        } else {
            return 3.0; // muito heterogêneo
        }
    }

    private Double calcularFrequenciaPicosPorHora(List<RegistroVelocidade> registros) {
        List<RegistroVelocidade> regs = registros.stream()
                .filter(r -> r.getVelocidadeRegistrada() != null 
                        && r.getRadar() != null 
                        && r.getRadar().getVelocidadePermitida() != null)
                .toList();

        if (regs.isEmpty()) {
            return 1.0;
        }

        Map<Integer, Long> picosPorHora = regs.stream()
                .filter(r -> r.getVelocidadeRegistrada() > r.getRadar().getVelocidadePermitida())
                .collect(Collectors.groupingBy(
                        r -> r.getData().getHour(),
                        Collectors.counting()
                ));

        long maxPicos = picosPorHora.values().stream()
                .max(Long::compareTo)
                .orElse(0L);

        if (maxPicos <= 5) {
            return 1.0; // poucos picos
        } else if (maxPicos <= 20) {
            return 2.0; // média de picos
        } else {
            return 3.0; // muitos picos
        }
    }

    private List<RegistroVelocidade> buscarRegistrosPorRegiao(Long regiaoId, String timestamp) {
        List<RegistroVelocidadeCache> cached =
                registroVelocidadeService.buscarRegistrosPorRegiao(regiaoId, timestamp);

        if (cached.isEmpty()) return List.of();

        List<String> radarIds = cached.stream()
                .map(RegistroVelocidadeCache::getRadarId)
                .distinct()
                .toList();

        List<Long> regiaoIds = cached.stream()
                .map(RegistroVelocidadeCache::getRegiaoId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, Radar> radarMap = radarRepository.findAllById(radarIds)
                .stream()
                .collect(Collectors.toMap(Radar::getRadarId, r -> r));

        Map<Long, Regiao> regiaoMap = regiaoRepository.findAllById(regiaoIds)
                .stream()
                .collect(Collectors.toMap(Regiao::getRegiaoId, r -> r));

        return cached.stream()
                .map(c -> {
                    RegistroVelocidade m = RegistroVelocidadeCache.ModelCache2Model(c);
                    m.setRadar(radarMap.get(c.getRadarId()));
                    m.setRegiao(regiaoMap.get(c.getRegiaoId()));
                    return m;
                })
                .toList();
    }

    // TODO: UTIL PARA DASHS

    // private Map<String, LocalTime> calcularHorarioDePico() {
    // List<Radar> radares = radarRepository.findAll();
    // Map<String, LocalTime> horarioPicoPorRadar = new HashMap<>();

    // for (Radar radar : radares) {
    // // Agrupa os registros por hora e conta quantos veículos passaram
    // List<RegistroVelocidade> registros =
    // registroVelocidadeRepository.findByRadar_RadarId(radar.getRadarId());
    // Map<Integer, Long> contagemPorHora = registros.stream()
    // .filter(r -> r.getData() != null)
    // .collect(Collectors.groupingBy(
    // r -> ((LocalDateTime) r.getData()).getHour(),
    // Collectors.counting()));

    // // Obtém a hora com maior contagem
    // Optional<Map.Entry<Integer, Long>> pico = contagemPorHora.entrySet().stream()
    // .max(Map.Entry.comparingByValue());

    // pico.ifPresent(entry -> horarioPicoPorRadar.put(radar.getRadarId(),
    // LocalTime.of(entry.getKey(), 0)));
    // }

    // return horarioPicoPorRadar;
    // }

    // TODO: UTIL PARA O MAPA

    // private Map<String, Long> calcularMapaDeFluxo() {
    // List<Radar> radares = radarRepository.findAll();
    // Map<String, Long> fluxoPorLocalizacao = new HashMap<>();

    // for (Radar radar : radares) {
    // Long total =
    // registroVelocidadeRepository.countByRadar_RadarId(radar.getRadarId());
    // String coordenadas = String.format("(%s, %s)", radar.getLatitude(),
    // radar.getLongitude());
    // fluxoPorLocalizacao.put(coordenadas, total);
    // }

    // return fluxoPorLocalizacao;
    // }

    // TODO: UTIL PARA DASHS

    // private Map<TipoVeiculo, Double>
    // calcularDistribuicaoPorTipo(List<RegistroVelocidade> registros) {
    // Map<TipoVeiculo, Double> distribuicao = new EnumMap<>(TipoVeiculo.class);
    // long totalVeiculos = registros.size();

    // if (totalVeiculos == 0) {
    // for (TipoVeiculo tipo : TipoVeiculo.values()) {
    // distribuicao.put(tipo, 0.0);
    // }
    // return distribuicao;
    // }

    // TODO: UTIL PARA DASHS

    // Map<TipoVeiculo, Long> contagemPorTipo = registros.stream()
    // .filter(r -> r.getTipoVeiculo() != null)
    // .collect(Collectors.groupingBy(RegistroVelocidade::getTipoVeiculo,
    // Collectors.counting()));

    // for (TipoVeiculo tipo : TipoVeiculo.values()) {
    // long contagemDoTipo = contagemPorTipo.getOrDefault(tipo, 0L);
    // double percentual = (double) contagemDoTipo / totalVeiculos * 100.0;
    // distribuicao.put(tipo, percentual);
    // }

    // return distribuicao;
    // }

    // TODO: UTIL PARA DASHS

    // private Map<PontoHoraKey, Double>
    // calcularVelocidadeMediaPorPontoEHora(List<RegistroVelocidade> registros) {
    // return registros.stream()
    // .filter(r -> r.getRadar() != null &&
    // r.getRadar().getRadarId() != null &&
    // r.getVelocidadeRegistrada() != null &&
    // r.getData() != null)

    // .collect(Collectors.groupingBy(
    // r -> new PontoHoraKey(
    // r.getRadar().getRadarId(),
    // r.getData().getHour()),

    // Collectors.averagingInt(RegistroVelocidade::getVelocidadeRegistrada)));
    // }

    // TODO: UTIL PARA DASHS

    // private Double calcularTaxaDeInfracoes(List<RegistroVelocidade> registros) {
    //     long totalVeiculos = registros.size();
    //     if (totalVeiculos == 0) {
    //         return 0.0;
    //     }

    //     long numeroDeInfracoes = registros.stream()
    //             .filter(r -> r.getVelocidadeRegistrada() != null &&
    //                     r.getRadar() != null &&
    //                     r.getRadar().getVelocidadePermitida() != null)
    //             .filter(r -> r.getVelocidadeRegistrada() > r.getRadar().getVelocidadePermitida())
    //             .count();

    //     return (double) numeroDeInfracoes / totalVeiculos * 100.0;
    // }
}
