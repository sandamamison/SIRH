package SI.RH.repository;

import SI.RH.entity.Pointage;
import SI.RH.entity.enums.DirectionPointage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointageRepository extends JpaRepository<Pointage, Long> {
    List<Pointage> findByEmployeId(Long employeId);
    List<Pointage> findByEmployeIdAndHorodatageBetweenOrderByHorodatageAsc(
            Long employeId, LocalDateTime debut, LocalDateTime fin);
    Optional<Pointage> findFirstByEmployeIdOrderByHorodatageDesc(Long employeId);
}
