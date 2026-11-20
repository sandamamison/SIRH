package SI.RH.repository;

import SI.RH.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoriqueSalaireRepository extends JpaRepository<HistoriqueSalaire, Long> {
    List<HistoriqueSalaire> findByEmployeIdOrderByDateChangementDesc(Long employeId);
}
