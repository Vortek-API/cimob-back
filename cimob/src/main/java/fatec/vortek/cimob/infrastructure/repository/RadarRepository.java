package fatec.vortek.cimob.infrastructure.repository;

import fatec.vortek.cimob.domain.model.Radar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RadarRepository extends JpaRepository<Radar, String> {
}
