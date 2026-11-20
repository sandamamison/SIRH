package SI.RH.repository;

import SI.RH.entity.EmployeCompetence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeCompetenceRepository extends JpaRepository<EmployeCompetence, Long> {
    List<EmployeCompetence> findByEmployeId(Long employeId);
}
