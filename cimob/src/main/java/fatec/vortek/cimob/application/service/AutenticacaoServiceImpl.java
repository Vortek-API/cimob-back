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

    public AuthResponse autenticar(String userName, String senha) {
        System.out.println("[LOGIN] Tentativa de login: " + userName);

        Usuario usuario = usuarioRepository.findByUserName(userName)
                .orElseThrow(() -> {
                    System.out.println("[LOGIN] Usuário não encontrado: " + userName);
                    return new RuntimeException("Usuário não encontrado");
                });

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            System.out.println("[LOGIN] Senha incorreta para usuário: " + userName);
            throw new RuntimeException("Senha incorreta");
        }

        String accessToken = jwtUtil.generateToken(usuario.getUserName());
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getUserName());

        usuario.setRefreshToken(refreshToken);
        usuarioRepository.save(usuario);

        System.out.println("[LOGIN] Login bem-sucedido para: " + userName);
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String refreshToken) {
        System.out.println("[REFRESH] Tentativa de refresh token: " + refreshToken);

        Usuario usuario = usuarioRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    System.out.println("[REFRESH] Refresh token inválido: " + refreshToken);
                    return new RuntimeException("Refresh token inválido");
                });

        String accessToken = jwtUtil.generateToken(usuario.getUserName());
        String newRefreshToken = jwtUtil.generateRefreshToken(usuario.getUserName());

        usuario.setRefreshToken(newRefreshToken);
        usuarioRepository.save(usuario);

        System.out.println("[REFRESH] Token atualizado para usuário: " + usuario.getUserName());
        return new AuthResponse(accessToken, newRefreshToken);
    }

    public void logout(String userName) {
        System.out.println("[LOGOUT] Tentativa de logout: " + userName);

        Usuario usuario = usuarioRepository.findByUserName(userName)
                .orElseThrow(() -> {
                    System.out.println("[LOGOUT] Usuário não encontrado: " + userName);
                    return new RuntimeException("Usuário não encontrado");
                });

        usuario.setRefreshToken(null);
        usuarioRepository.save(usuario);

        System.out.println("[LOGOUT] Logout realizado: " + userName);
    }
}
