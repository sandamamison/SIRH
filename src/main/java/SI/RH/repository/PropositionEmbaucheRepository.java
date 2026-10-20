package SI.RH.repository;
import SI.RH.entity.PropositionEmbauche; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface PropositionEmbaucheRepository extends JpaRepository<PropositionEmbauche,Long> { List<PropositionEmbauche> findByCandidatureId(Long candidatureId); Optional<PropositionEmbauche> findByCandidatureIdAndStatutIn(Long candidatureId, Collection<SI.RH.entity.enums.StatutProposition> statuts); }
