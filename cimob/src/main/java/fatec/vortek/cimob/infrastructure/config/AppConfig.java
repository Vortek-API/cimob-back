package fatec.vortek.cimob.infrastructure.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    // Janela de tempo em minutos para busca de dados
    public static final int TIME_WINDOW_MINUTES = 5;
    
    // Configurações da API
    public static final int API_TIMEOUT_SECONDS = 10;
    public static final int MAX_RETRY_ATTEMPTS = 3;
    
    // Configurações de cache
    public static final int CACHE_TTL_MINUTES = 1;
    
    // Método helper para obter a janela de tempo em minutos
    public static int getTimeWindowMinutes() {
        return TIME_WINDOW_MINUTES;
    }
    
    // Método helper para obter informações da configuração
    public static String getConfigInfo() {
        return String.format("Time Window: %d minutes, API Timeout: %d seconds", 
                           TIME_WINDOW_MINUTES, API_TIMEOUT_SECONDS);
    }
}
