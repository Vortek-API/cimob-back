package fatec.vortek.cimob.infrastructure.repository;

import fatec.vortek.cimob.domain.model.Regiao;
import fatec.vortek.cimob.domain.model.Timeline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimelineRepository extends JpaRepository<Timeline, Long> {
}
