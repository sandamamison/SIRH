package SI.RH.repository;

import SI.RH.entity.VariablePaie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariablePaieRepository extends JpaRepository<VariablePaie, Long> {
    List<VariablePaie> findByPeriodePaieIdAndEmployeId(Long periodePaieId, Long employeId);
    List<VariablePaie> findByPeriodePaieId(Long periodePaieId);
}
