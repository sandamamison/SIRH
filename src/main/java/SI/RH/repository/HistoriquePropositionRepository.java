package SI.RH.repository;
import SI.RH.entity.HistoriqueProposition; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface HistoriquePropositionRepository extends JpaRepository<HistoriqueProposition,Long> { List<HistoriqueProposition> findByPropositionIdOrderByDateTransitionAsc(Long propositionId); }
