package fatec.vortek.cimob.domain.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.service.IndicadorService;
import fatec.vortek.cimob.domain.service.TelegramService;
import fatec.vortek.cimob.presentation.dto.request.TelegramMessageDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndicadorScheduler {

    private final IndicadorService indicadorService;
    private final TelegramService telegramService;

    /**
     * Executa a cada 60 segundos.
     * Você pode mudar o tempo para cron quando quiser.
     */
    @Scheduled(fixedRate = 60_000)
    public void verificarIndicadoresPeriodicamente() {

        System.out.println("[Scheduler] Verificando indicadores...");

        List<Indicador> indicadores = indicadorService.listarTodos();

        indicadores.forEach(ind -> {

            // Se indicador.getValor() == 3 → situação crítica
            if (ind.getValor() != null && ind.getValor() >= 3) {

                telegramService.enviarMensagem(
                    new TelegramMessageDTO(
                        "🚨 ALERTA DE TRÂNSITO\n" +
                        "Indicador: " + ind.getMnemonico() + "\n" +
                        "Nível: CRÍTICO (3)\n" +
                        "Horário: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                )
            );
            }
        });
    }
}

