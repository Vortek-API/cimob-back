package fatec.vortek.cimob.application.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fatec.vortek.cimob.domain.service.TelegramService;
import fatec.vortek.cimob.presentation.dto.request.TelegramMessageDTO;

@Service
public class TelegramServiceImpl implements TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private static final String TELEGRAM_URL = "https://api.telegram.org/bot%s/sendMessage";

    @Override
    public void enviarMensagem(TelegramMessageDTO dto) {
        String url = String.format(TELEGRAM_URL, botToken);

        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", dto.message());

        restTemplate.postForObject(url, body, String.class);
    }

}
