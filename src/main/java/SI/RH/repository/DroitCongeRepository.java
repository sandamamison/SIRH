package SI.RH.repository;

import SI.RH.entity.DroitConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroitCongeRepository extends JpaRepository<DroitConge, Long> {
    List<DroitConge> findByEmployeId(Long employeId);
    Optional<DroitConge> findByEmployeIdAndTypeCongeIdAndAnnee(Long employeId, Long typeCongeId, Integer annee);
}
