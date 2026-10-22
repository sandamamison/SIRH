package SI.RH.repository;

import SI.RH.entity.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AffectationRepository extends JpaRepository<Affectation, Integer> {

    List<Affectation> findByEmployeIdOrderByDateDebutDesc(Integer employeId);

    Optional<Affectation> findFirstByEmployeIdAndDateFinIsNull(Integer employeId);

    List<Affectation> findByPosteIdOrderByDateDebutDesc(Integer posteId);

    boolean existsByPosteIdAndDateFinIsNull(Integer posteId);

    boolean existsByEmployeIdAndDateFinIsNull(Integer employeId);
}