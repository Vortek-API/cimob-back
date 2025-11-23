package fatec.vortek.cimob.domain.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fatec.vortek.cimob.application.service.IndicadorServiceImpl;
import fatec.vortek.cimob.application.service.TelegramServiceImpl;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.infrastructure.repository.RegiaoRepository;
import fatec.vortek.cimob.presentation.dto.request.TelegramMessageDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndicadorScheduler {

    private final IndicadorServiceImpl indicadorService;
    private final TelegramServiceImpl telegramService;
    private final RegiaoRepository regiaoRepository;

    /**
     * Executa a cada 5 minutos.
     */
    @Transactional
    @Scheduled(fixedRate = 300_000)
    public void verificarIndicadoresPeriodicamente() {

        System.out.println("[Scheduler] Verificando indicadores...");

        
        List<Regiao> regioes = regiaoRepository.findAll();
        //List<Indicador> indicadores = indicadorRepository.findAll();
        
        for (Regiao regiao : regioes) {
            List<Indicador> indicadores = indicadorService.listarPorRegiao(regiao.getRegiaoId());

            for (Indicador indicador : indicadores) {

                // Se indicador.getValor() == 3 → situação crítica
                if (indicador.getValor() != null && indicador.getValor() >= 3) {

                
                    telegramService.enviarMensagem(
                    new TelegramMessageDTO(
                        "🚨 ALERTA DE TRÂNSITO\n" +
                                "Indicador: " + indicador.getNome() + "\n" +
                                "Nível: CRÍTICO (3)\n" +
                                "Região: " + regiao.getNome() + "\n" +
                                "Horário: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                    ));
                }
            }
        }
    }
}

