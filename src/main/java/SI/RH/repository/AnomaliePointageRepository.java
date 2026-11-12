package SI.RH.repository;

import SI.RH.entity.AnomaliePointage;
import SI.RH.entity.enums.StatutAnomalie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnomaliePointageRepository extends JpaRepository<AnomaliePointage, Long> {
    List<AnomaliePointage> findByEmployeId(Long employeId);
    List<AnomaliePointage> findByEmployeIdAndStatut(Long employeId, StatutAnomalie statut);
    List<AnomaliePointage> findByStatut(StatutAnomalie statut);
}
