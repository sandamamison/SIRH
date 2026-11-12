package SI.RH.repository;

import SI.RH.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByEmployeId(Long employeId);
    List<Absence> findByEmployeIdAndDateDebutBetween(Long employeId, LocalDate debut, LocalDate fin);
}
