package SI.RH.repository;

import SI.RH.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentDisciplinaireRepository extends JpaRepository<IncidentDisciplinaire, Long> {
    List<IncidentDisciplinaire> findByEmployeId(Long employeId);
}
