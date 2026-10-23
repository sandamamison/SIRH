package SI.RH.repository;

import SI.RH.entity.ValidationDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ValidationDemandeRepository extends JpaRepository<ValidationDemande, Integer> {
    List<ValidationDemande> findByDemandeRecrutementIdOrderByDateDecision(Integer demandeRecrutementId);
}
