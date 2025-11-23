package fatec.vortek.cimob.infrastructure.repository;

import fatec.vortek.cimob.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public boolean existsByCpf(String cpf);
    public Optional<Usuario> findByEmail(String email);

    public Optional<Usuario> findByRefreshToken(String refreshToken);
    public Optional<Usuario> findByResetToken(String resetToken);

}
