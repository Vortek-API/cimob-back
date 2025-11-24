package fatec.vortek.cimob.domain.service;

import fatec.vortek.cimob.presentation.dto.request.TelegramMessageDTO;

public interface TelegramService {
    void enviarMensagem(TelegramMessageDTO dto);
}
