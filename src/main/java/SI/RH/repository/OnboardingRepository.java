package SI.RH.repository;

import SI.RH.entity.Onboarding;
import SI.RH.entity.enums.StatutOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {
    List<Onboarding> findByEmployeId(Long employeId);
    Optional<Onboarding> findFirstByEmployeIdOrderByCreatedAtDesc(Long employeId);
    List<Onboarding> findByStatut(StatutOnboarding statut);
}
