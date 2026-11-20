package SI.RH.repository;

import SI.RH.entity.HistoriqueAffectationCarriere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueAffectationCarriereRepository
        extends JpaRepository<HistoriqueAffectationCarriere, Long> {
    List<HistoriqueAffectationCarriere> findByEmployeId(Long employeId);
}
