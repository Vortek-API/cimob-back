package fatec.vortek.cimob.infrastructure.repository;

import fatec.vortek.cimob.domain.enums.IndicadorMnemonico;
import fatec.vortek.cimob.domain.model.Indicador;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicadorRepository extends JpaRepository<Indicador, Long> {

    Indicador findByMnemonico(IndicadorMnemonico mnemonico);
    
    @Modifying
    @Transactional
    @Query("UPDATE Indicador i SET i.oculto = CASE WHEN i.indicadorId IN :ids THEN 'N' ELSE 'S' END")
    void updateOcultoByIdsCase(@Param("ids") List<Long> ids);

}
