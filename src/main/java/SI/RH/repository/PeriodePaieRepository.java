package SI.RH.repository;

import SI.RH.entity.PeriodePaie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeriodePaieRepository extends JpaRepository<PeriodePaie, Long> {
    Optional<PeriodePaie> findByCode(String code);
    Optional<PeriodePaie> findByMoisAndAnnee(Integer mois, Integer annee);
}
