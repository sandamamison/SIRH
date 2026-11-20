package SI.RH.repository;

import SI.RH.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProcedureDisciplinaireRepository extends JpaRepository<ProcedureDisciplinaire, Long> {
    List<ProcedureDisciplinaire> findByIncidentId(Long incidentId);
}
