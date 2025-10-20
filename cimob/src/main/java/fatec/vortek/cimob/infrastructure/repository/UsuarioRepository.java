package fatec.vortek.cimob.infrastructure.repository;

import fatec.vortek.cimob.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

   boolean existsByCpf(String cpf);
    Optional<Usuario> findByUserName(String userName);

    Optional<Usuario> findByRefreshToken(String refreshToken);

}
