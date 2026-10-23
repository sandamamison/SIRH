package SI.RH.repository;

import SI.RH.entity.EvaluationCandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvaluationCandidatRepository
        extends JpaRepository<EvaluationCandidat, Long> {
    List<EvaluationCandidat> findByEntretienId(Long entretienId);
    boolean existsByEntretienIdAndEmployeEvaluateurId(
            Long entretienId, Long employeEvaluateurId);
}
