package SI.RH.repository;

import SI.RH.entity.DemandeRecrutement;
import SI.RH.entity.enums.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeRecrutementRepository extends JpaRepository<DemandeRecrutement, Integer> {
    List<DemandeRecrutement> findByStatutOrderByDateCreationDesc(StatutDemande statut);
    List<DemandeRecrutement> findByEmployeDemandeurIdOrderByDateCreationDesc(Integer employeDemandeurId);
    List<DemandeRecrutement> findByStatutAndEmployeDemandeurIdOrderByDateCreationDesc(StatutDemande statut, Integer employeDemandeurId);
    List<DemandeRecrutement> findAllByOrderByDateCreationDesc();
}
