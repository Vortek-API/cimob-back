package fatec.vortek.cimob.application.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import fatec.vortek.cimob.domain.model.Usuario;
import fatec.vortek.cimob.domain.service.EmailService;
import fatec.vortek.cimob.infrastructure.config.JwtUtilConfig;
import fatec.vortek.cimob.infrastructure.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticacaoServiceImpl {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtilConfig jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UsuarioServiceImpl usuarioService;
    private final TimelineServiceImpl timelineService;
    private final EmailService emailService;

    public record AuthResponse(String accessToken) {}

    public AuthResponse autenticar(String email, String senha) {
        System.out.println("[LOGIN] Tentativa de login: " + email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[LOGIN] Usuário não encontrado: " + email);
                    return new RuntimeException("Usuário não encontrado");
                });

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            System.out.println("[LOGIN] Senha incorreta para usuário: " + email);
            throw new RuntimeException("Senha incorreta");
        }

        String accessToken = jwtUtil.generateToken(usuario.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getEmail());

        usuario.setRefreshToken(refreshToken);
        usuario.setAccessToken(accessToken);
        usuarioRepository.save(usuario);

        System.out.println("[LOGIN] Login bem-sucedido para: " + email);
        timelineService.criarTimelineLogin(usuario);
        return new AuthResponse(accessToken);
    }

    public AuthResponse refreshToken(String refreshToken) {
        System.out.println("[REFRESH] Tentativa de refresh token: " + refreshToken);

        Usuario usuario = usuarioRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    System.out.println("[REFRESH] Refresh token inválido: " + refreshToken);
                    return new RuntimeException("Refresh token inválido");
                });

        String accessToken = jwtUtil.generateToken(usuario.getEmail());
        String newRefreshToken = jwtUtil.generateRefreshToken(usuario.getEmail());

        usuario.setRefreshToken(newRefreshToken);
        usuario.setAccessToken(accessToken);
        usuarioRepository.save(usuario);

        System.out.println("[REFRESH] Token atualizado para usuário: " + usuario.getEmail());
        return new AuthResponse(accessToken);
    }

    public void logout(String email) {
        System.out.println("[LOGOUT] Tentativa de logout: " + email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[LOGOUT] Usuário não encontrado: " + email);
                    return new RuntimeException("Usuário não encontrado");
                });

        usuario.setAccessToken(null);
        usuarioRepository.save(usuario);

        timelineService.criarTimelineLogout(usuarioService.getUsuarioLogado());
        System.out.println("[LOGOUT] Logout realizado: " + email);
    }

    public void recuperarSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = java.util.UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setResetTokenExpiration(java.time.LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        emailService.sendPasswordResetEmail(usuario.getEmail(), token);
    }

    public void redefinirSenha(String token, String novaSenha) {
        Usuario usuario = usuarioRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (usuario.getResetTokenExpiration().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setResetToken(null);
        usuario.setResetTokenExpiration(null);
        usuarioRepository.save(usuario);
    }
}
