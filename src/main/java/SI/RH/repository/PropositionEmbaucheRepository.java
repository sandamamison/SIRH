package SI.RH.repository;

import SI.RH.entity.PropositionEmbauche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropositionEmbaucheRepository extends JpaRepository<PropositionEmbauche, Long> {

    List<PropositionEmbauche> findByCandidatureId(Long candidatureId);
}
