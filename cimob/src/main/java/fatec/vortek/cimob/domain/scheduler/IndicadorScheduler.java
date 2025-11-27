package fatec.vortek.cimob.domain.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.domain.service.RegiaoService;

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

            for (Indicador ind : indicadores) {
                // Se indicador.getValor() >= 3 → situação crítica
                if (ind.getValor() != null && ind.getValor() >= 3) {
                    String mensagem = formatarMensagemAlerta(ind, regiao);
                    telegramService.enviarMensagem(new TelegramMessageDTO(mensagem));
                }
            }
        }
    }


    /**
     * Formata a mensagem de alerta na versão "Refined Classic",
     * combinando a estrutura de bordas com formatação moderna e limpa.
     */
    private String formatarMensagemAlerta(Indicador indicador, Regiao regiao) {
        LocalDateTime agora = LocalDateTime.now();
        
        // Formatadores de data/hora
        String dataHora = agora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String diaSemana = agora.format(DateTimeFormatter.ofPattern("EEEE", new Locale("pt", "BR")));
        
        // Informações do indicador
        String nomeRegiao = (regiao != null && regiao.getNome() != null) ? regiao.getNome() : "Não especificada";
        Integer nivelCritico = indicador.getValor() != null
        ? indicador.getValor().intValue()
        : null;
        String mnemonico = indicador.getMnemonico().name();
        
        // Determina emoji, texto do nível de severidade e recomendação
        String emojiNivel = obterEmojiNivel(nivelCritico);
        String textoNivel = obterTextoNivel(nivelCritico);
        String recomendacao = obterRecomendacao(nivelCritico);
        
        // Monta a mensagem formatada
        StringBuilder msg = new StringBuilder();
        
        // Cabeçalho: Destaque com borda e emojis de impacto
        msg.append("🚨 *ALERTA DE TRÂNSITO* 🚨\n");
        msg.append("╔═══════════════════════════════╗\n");
        msg.append(String.format("║ %s %s | NÍVEL %d/3 %s ║\n", emojiNivel, textoNivel, nivelCritico, obterBarraProgresso(nivelCritico)));
        msg.append("╚═══════════════════════════════╝\n\n");
        
        // Detalhes: Informações essenciais
        msg.append("📍 *Região:* " + nomeRegiao + "\n");
        msg.append("📊 *Indicador:* `" + mnemonico + "`\n");
        msg.append("📅 *Registro:* " + dataHora + " (" + capitalize(diaSemana) + ")\n\n");
        
        // Ação: Recomendação em destaque
        msg.append("💡 *Ação Imediata:*\n");
        
        // Chamada de atenção ao responsável
        msg.append(String.format("📢 *ATENÇÃO AO RESPONSÁVEL DA REGIÃO %s:*\n", nomeRegiao.toUpperCase()));
        msg.append(recomendacao + "\n\n");
        
        // Rodapé: Assinatura e separador
        msg.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        msg.append("🤖 *CIMOB Sistema de Monitoramento*\n");
        msg.append("_Alerta gerado automaticamente_");
        
        return msg.toString();
    }

    /**
     * Retorna emoji apropriado baseado no nível crítico
     */
    private String obterEmojiNivel(Integer nivel) {
        if (nivel == null) return "⚪";
        return switch (nivel) {
            case 3 -> "🔴"; // Crítico
            case 2 -> "🟡"; // Atenção
            default -> "🔵"; // Normal
        };
    }

    /**
     * Retorna texto descritivo do nível
     */
    private String obterTextoNivel(Integer nivel) {
        if (nivel == null) return "INDEFINIDO";
        return switch (nivel) {
            case 3 -> "CRÍTICO";
            case 2 -> "ATENÇÃO";
            default -> "NORMAL";
        };
    }

    /**
     * Retorna barra de progresso visual
     */
    private String obterBarraProgresso(Integer nivel) {
        if (nivel == null) return "░░░░░";
        
        StringBuilder barra = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= nivel) {
                barra.append("█"); // Bloco sólido
            } else {
                barra.append("░"); // Bloco vazio
            }
        }
        return barra.toString();
    }

    /**
     * Retorna recomendação baseada no nível
     */
    private String obterRecomendacao(Integer nivel) {
        if (nivel == null) return "⚠️ Aguarde mais informações";
        
        return switch (nivel) {
            case 3 -> "*AÇÃO URGENTE:* Avaliar rotas alternativas. Congestionamento severo. Considere adiar o deslocamento.";
            case 2 -> "*AÇÃO DE MONITORAMENTO:* Planejar tempo extra (+30 min). Trânsito intenso. Mantenha a vigilância.";
            default -> "*AÇÃO PADRÃO:* Fluxo normal. Prossiga com segurança.";
        };
    }

   
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
