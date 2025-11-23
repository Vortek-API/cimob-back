package fatec.vortek.cimob.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import fatec.vortek.cimob.domain.service.TelegramService;
import fatec.vortek.cimob.presentation.dto.request.TelegramMessageDTO;

@RestController
@RequestMapping("/telegram")
public class TelegramController {

    @Autowired
    private TelegramService telegramService;

    @PostMapping("/send")
    public void enviar(@RequestBody TelegramMessageDTO dto) {
        telegramService.enviarMensagem(dto);
    }
}
