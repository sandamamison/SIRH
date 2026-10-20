package SI.RH.repository;

import SI.RH.entity.HistoriqueStatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueStatutDemandeRepository extends JpaRepository<HistoriqueStatutDemande, Integer> {
    List<HistoriqueStatutDemande> findByDemandeRecrutementIdOrderByDateTransition(Integer demandeRecrutementId);
}
