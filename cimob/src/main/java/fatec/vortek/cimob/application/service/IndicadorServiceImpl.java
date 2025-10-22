package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.enums.IndicadorMnemonico;
import fatec.vortek.cimob.domain.model.Evento;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.domain.model.Radar;
import fatec.vortek.cimob.domain.model.RegistroVelocidade;
import fatec.vortek.cimob.domain.service.IndicadorService;
import fatec.vortek.cimob.domain.service.RegiaoService;
import fatec.vortek.cimob.infrastructure.repository.IndicadorRepository;
import fatec.vortek.cimob.infrastructure.repository.EventoRepository;
import fatec.vortek.cimob.infrastructure.repository.RegistroVelocidadeRepository;
import fatec.vortek.cimob.infrastructure.config.AppConfig;
import fatec.vortek.cimob.presentation.dto.response.IndiceCriticoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class IndicadorServiceImpl implements IndicadorService {

    private final IndicadorRepository repository;
    private final EventoRepository eventoRepository;
    private final RegiaoService regiaoService;
    private final RegistroVelocidadeRepository registroVelocidadeRepository;

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
    public List<Indicador> listarTodos(String timestamp) {
        List<Indicador> indicadores = repository.findAll().stream()
                .filter(ind -> !"S".equals(ind.getDeletado()))
                .collect(Collectors.toList());

        List<RegistroVelocidade> registros = buscarRegistrosPorRegiao(null, timestamp);

        indicadores.forEach(indicador -> calcularValorIndicadoresComRegistros(indicador, registros));

        return indicadores;
    }

    @Override
    public List<Indicador> listarPorRegiao(Long regiaoId) {
        return listarPorRegiao(regiaoId, null);
    }
    
    @Override
    public List<Indicador> listarPorRegiao(Long regiaoId, String timestamp) {
        Regiao regiao = regiaoService.buscarPorId(regiaoId);
        if (regiao == null) {
            throw new RuntimeException("Região não encontrada com ID: " + regiaoId);
        }
        
        List<Indicador> todosIndicadores = repository.findAll().stream()
                .filter(indicador -> !"S".equals(indicador.getDeletado()))
                .collect(Collectors.toList());

        List<RegistroVelocidade> registros = buscarRegistrosPorRegiao(regiaoId, timestamp);

        todosIndicadores.forEach(indicador -> calcularValorIndicadoresComRegistros(indicador, registros));

        return todosIndicadores;
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
    public void associarAEvento(Long indicadorId, Long eventoId) {
        Indicador i = repository.findById(indicadorId).orElseThrow();
        Evento e = eventoRepository.findById(eventoId).orElseThrow();
        i.getEventos().add(e);
        repository.save(i);
    }

    @Override
    public void desassociarDeEvento(Long indicadorId, Long eventoId) {
        Indicador i = repository.findById(indicadorId).orElseThrow();
        Evento e = eventoRepository.findById(eventoId).orElseThrow();
        i.getEventos().remove(e);
        repository.save(i);
    }

    @Override
    public List<Evento> listarEventos(Long indicadorId) {
        Indicador i = repository.findById(indicadorId).orElseThrow();
        return i.getEventos();
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
                .filter(r -> r.getVelocidadeRegistrada() != null && r.getRadar() != null && r.getRadar().getVelocidadePermitida() != null && r.getData() != null)
                .forEach(r -> {
                    Radar rad = r.getRadar();
                    String endereco = rad.getEndereco() != null ? rad.getEndereco() : "";
                    Integer velPerm = rad.getVelocidadePermitida();
                    Long regIdVal = rad.getRegiao() != null ? rad.getRegiao().getRegiaoId() : null;
                    String regNomeVal = rad.getRegiao() != null ? rad.getRegiao().getNome() : null;
                    String key = endereco + "|" + velPerm + "|" + regIdVal + "|" + regNomeVal;
                    Agg agg = mapa.computeIfAbsent(key, k -> { Agg a = new Agg(); a.velPerm = velPerm; a.regiaoId = regIdVal; a.regiaoNome = regNomeVal; a.endereco = endereco; return a; });
                    if (r.getVelocidadeRegistrada() > velPerm) {
                        agg.somaVel += r.getVelocidadeRegistrada();
                        agg.cont += 1;
                        LocalDateTime dt = r.getData();
                        if (agg.minInf == null || dt.isBefore(agg.minInf)) agg.minInf = dt;
                        if (agg.maxInf == null || dt.isAfter(agg.maxInf)) agg.maxInf = dt;
                    }
                });

        DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern("HH'h'mm");

        return mapa.values().stream()
                .filter(a -> a.cont > 0)
                .map(a -> {
                    double media = a.somaVel / a.cont;
                    double excessoMedio = media - a.velPerm;
                    String intervalo = (a.minInf != null && a.maxInf != null) ? ("das " + a.minInf.format(horaFmt) + " às " + a.maxInf.format(horaFmt)) : null;
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
    
    
    
    private List<RegistroVelocidade> buscarRegistrosPorRegiao(Long regiaoId, String timestamp) {
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

        if (regiaoId == null) {
            return registroVelocidadeRepository.findByDataBetweenAndDeletado(inicioPeriodo, fimPeriodo);
        } else {
            return registroVelocidadeRepository.findByDataBetweenAndRegiaoAndDeletado(inicioPeriodo, fimPeriodo, regiaoId);
        }
    }
}
