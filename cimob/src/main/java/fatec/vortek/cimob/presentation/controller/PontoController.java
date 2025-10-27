package fatec.vortek.cimob.presentation.controller;

import fatec.vortek.cimob.presentation.dto.response.PontoResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import fatec.vortek.cimob.application.service.PontoServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/pontos")
@RequiredArgsConstructor
@Tag(name = "Pontos")
public class PontoController {

    private final PontoServiceImpl pontoService;

    @GetMapping()
    public List<PontoResponseDTO> listarTodos() {
        return pontoService.listarTodos();
    }
    @GetMapping("/{regiaoId}")
    public List<PontoResponseDTO> listarTodosPorRegiaoId(@PathVariable Long regiaoId) {
        return pontoService.listarTodosPorRegiaoId(regiaoId);
    }
}
