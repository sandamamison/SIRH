package SI.RH.repository;

import SI.RH.entity.HistoriqueStatutCandidature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueStatutCandidatureRepository
        extends JpaRepository<HistoriqueStatutCandidature, Long> {

    List<HistoriqueStatutCandidature> findByCandidatureIdOrderByDateTransitionAsc(
            Long candidatureId);
}
