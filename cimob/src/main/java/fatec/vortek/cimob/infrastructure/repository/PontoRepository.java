package fatec.vortek.cimob.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fatec.vortek.cimob.domain.model.Ponto;

@Repository
public interface PontoRepository extends JpaRepository<Ponto, Long> {
    
    List<Ponto> findByRegiao_RegiaoId(Long regiaoId);
}
