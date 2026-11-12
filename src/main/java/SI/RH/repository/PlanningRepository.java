package SI.RH.repository;

import SI.RH.entity.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanningRepository extends JpaRepository<Planning, Long> {
    List<Planning> findByEmployeId(Long employeId);
    Optional<Planning> findByEmployeIdAndSemaineDebut(Long employeId, LocalDate semaineDebut);
}
