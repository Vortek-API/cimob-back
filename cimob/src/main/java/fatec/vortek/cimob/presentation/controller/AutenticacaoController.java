package fatec.vortek.cimob.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fatec.vortek.cimob.application.service.AutenticacaoServiceImpl;
import fatec.vortek.cimob.application.service.UsuarioServiceImpl;
import fatec.vortek.cimob.presentation.dto.request.LoginRequestDTO;
import fatec.vortek.cimob.presentation.dto.request.UsuarioRequestDTO;
import fatec.vortek.cimob.presentation.dto.response.LoginResponseDTO;
import fatec.vortek.cimob.presentation.dto.response.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoServiceImpl authService;
    private final UsuarioServiceImpl usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            var auth = authService.autenticar(request.getEmail(), request.getSenha());
            return ResponseEntity.ok(new LoginResponseDTO(auth.accessToken()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshToken) {
        try {
            var auth = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(new LoginResponseDTO(auth.accessToken()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastro(@RequestBody UsuarioRequestDTO request) {
        try {
            var usuario = usuarioService.criar(request);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String userName) {
        try {
            authService.logout(userName);
            return ResponseEntity.ok("Logout realizado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
