package fatec.vortek.cimob.infrastructure.repository;

import fatec.vortek.cimob.domain.model.RegistroVelocidade;
import fatec.vortek.cimob.domain.enums.TipoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroVelocidadeRepository extends JpaRepository<RegistroVelocidade, Long> {
    
    @Query("SELECT r FROM RegistroVelocidade r WHERE r.deletado = 'N' AND r.data >= :inicioDia AND r.data <= :fimDia")
    List<RegistroVelocidade> findByDataBetweenAndDeletado(@Param("inicioDia") LocalDateTime inicioDia, 
                                                         @Param("fimDia") LocalDateTime fimDia);
    
    @Query("SELECT r FROM RegistroVelocidade r " +
           "JOIN r.radar rad " +
           "WHERE r.deletado = 'N' AND r.data >= :inicioDia AND r.data <= :fimDia " +
           "AND rad.regiao.regiaoId = :regiaoId AND rad.deletado = 'N'")
    List<RegistroVelocidade> findByDataBetweenAndRegiaoAndDeletado(@Param("inicioDia") LocalDateTime inicioDia,
                                                                   @Param("fimDia") LocalDateTime fimDia,
                                                                   @Param("regiaoId") Long regiaoId);

    @Query("SELECT r FROM RegistroVelocidade r WHERE r.deletado = 'N' AND r.data >= :inicioDia AND r.data <= :fimDia")
    List<RegistroVelocidade> findByDiaInteiroAndDeletado(@Param("inicioDia") LocalDateTime inicioDia,
                                                         @Param("fimDia") LocalDateTime fimDia);

    @Query("SELECT r FROM RegistroVelocidade r JOIN r.radar rad WHERE r.deletado = 'N' AND r.data >= :inicioDia AND r.data <= :fimDia AND rad.regiao.regiaoId = :regiaoId AND rad.deletado = 'N'")
    List<RegistroVelocidade> findByDiaInteiroAndRegiaoAndDeletado(@Param("inicioDia") LocalDateTime inicioDia,
                                                                  @Param("fimDia") LocalDateTime fimDia,
                                                                  @Param("regiaoId") Long regiaoId);

       @Query("SELECT r FROM RegistroVelocidade r WHERE r.deletado = 'N' AND r.data >= :inicioDia AND r.data <= :fimDia AND r.tipoVeiculo = :tipoVeiculo")
       List<RegistroVelocidade> findByDataBetweenAndTipoVeiculo(@Param("inicioDia") LocalDateTime inicioDia,
                                                                                                                @Param("fimDia") LocalDateTime fimDia,
                                                                                                                @Param("tipoVeiculo") TipoVeiculo tipoVeiculo);

       @Query("SELECT r FROM RegistroVelocidade r JOIN r.radar rad WHERE r.deletado = 'N' AND r.data >= :inicioDia AND r.data <= :fimDia AND rad.regiao.regiaoId = :regiaoId AND rad.deletado = 'N' AND r.tipoVeiculo = :tipoVeiculo")
       List<RegistroVelocidade> findByDataBetweenAndRegiaoAndTipoVeiculo(@Param("inicioDia") LocalDateTime inicioDia,
                                                                                                                         @Param("fimDia") LocalDateTime fimDia,
                                                                                                                         @Param("regiaoId") Long regiaoId,
                                                                                                                         @Param("tipoVeiculo") TipoVeiculo tipoVeiculo);

       Long countByRadar_RadarIdAndDataBetween(String radarId, LocalDateTime cincoMinAtras, LocalDateTime agora);

       List<RegistroVelocidade> findByRadar_RadarId(String radarId);

       Long countByRadar_RadarId(String radarId);

       @Query("SELECT r FROM RegistroVelocidade r " +
              "JOIN r.radar rad " +
              "WHERE r.deletado = 'N' AND rad.deletado = 'N' " +
              "AND (:radarId IS NULL OR rad.radarId = :radarId) " +
              "AND (:todasRegioes = true OR :regiaoId IS NULL OR rad.regiao.regiaoId = :regiaoId) " +
              "AND (:inicioDia IS NULL OR r.data >= :inicioDia) " +
              "AND (:fimDia IS NULL OR r.data <= :fimDia)")
       List<RegistroVelocidade> findByFiltros(@Param("radarId") String radarId,
                                              @Param("regiaoId") Long regiaoId,
                                              @Param("todasRegioes") boolean todasRegioes,
                                              @Param("inicioDia") LocalDateTime inicioDia,
                                              @Param("fimDia") LocalDateTime fimDia);
}
