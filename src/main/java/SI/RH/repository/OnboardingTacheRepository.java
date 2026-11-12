package SI.RH.repository;

import SI.RH.entity.OnboardingTache;
import SI.RH.entity.enums.StatutTache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingTacheRepository extends JpaRepository<OnboardingTache, Long> {
    List<OnboardingTache> findByOnboardingId(Long onboardingId);
    List<OnboardingTache> findByOnboardingIdAndObligatoireTrue(Long onboardingId);
    long countByOnboardingIdAndObligatoireTrueAndStatutNot(Long onboardingId, StatutTache statut);
}
