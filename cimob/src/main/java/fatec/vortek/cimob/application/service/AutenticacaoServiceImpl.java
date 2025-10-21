package fatec.vortek.cimob.application.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import fatec.vortek.cimob.domain.model.Usuario;
import fatec.vortek.cimob.infrastructure.config.JwtUtilConfig;
import fatec.vortek.cimob.infrastructure.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticacaoServiceImpl {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtilConfig jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public record AuthResponse(String accessToken, String refreshToken) {}

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
        usuarioRepository.save(usuario);

        System.out.println("[LOGIN] Login bem-sucedido para: " + email);
        return new AuthResponse(accessToken, refreshToken);
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
        usuarioRepository.save(usuario);

        System.out.println("[REFRESH] Token atualizado para usuário: " + usuario.getEmail());
        return new AuthResponse(accessToken, newRefreshToken);
    }

    public void logout(String email) {
        System.out.println("[LOGOUT] Tentativa de logout: " + email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[LOGOUT] Usuário não encontrado: " + email);
                    return new RuntimeException("Usuário não encontrado");
                });

        usuario.setRefreshToken(null);
        usuarioRepository.save(usuario);

        System.out.println("[LOGOUT] Logout realizado: " + email);
    }
}
