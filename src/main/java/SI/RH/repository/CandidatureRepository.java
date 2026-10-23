package SI.RH.repository;

import SI.RH.entity.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    boolean existsByOffreEmploiIdAndPersonneId(
            Long offreEmploiId,
            Long personneId
    );
long countByOffreEmploiIdAndStatut(
        Long offreEmploiId,
        SI.RH.entity.enums.StatutCandidature statut
);
}
