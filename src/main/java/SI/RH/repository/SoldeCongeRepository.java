package SI.RH.repository;

import SI.RH.entity.SoldeConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoldeCongeRepository extends JpaRepository<SoldeConge, Long> {
    List<SoldeConge> findByEmployeId(Long employeId);
    Optional<SoldeConge> findByEmployeIdAndTypeCongeId(Long employeId, Long typeCongeId);
}
