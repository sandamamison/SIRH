package SI.RH.repository;

import SI.RH.entity.Entretien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EntretienRepository extends JpaRepository<Entretien, Long> {
    List<Entretien> findByCandidatureId(Long candidatureId);
}
