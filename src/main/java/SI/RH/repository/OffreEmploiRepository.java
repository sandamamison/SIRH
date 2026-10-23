package SI.RH.repository;

import SI.RH.entity.OffreEmploi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OffreEmploiRepository extends JpaRepository<OffreEmploi, Integer> {

    Optional<OffreEmploi> findByDemandeRecrutementId(Integer demandeRecrutementId);

    boolean existsByDemandeRecrutementId(Integer demandeRecrutementId);
}
