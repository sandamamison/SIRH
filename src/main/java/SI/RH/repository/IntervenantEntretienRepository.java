package SI.RH.repository;

import SI.RH.entity.IntervenantEntretien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IntervenantEntretienRepository
        extends JpaRepository<IntervenantEntretien, Long> {
    List<IntervenantEntretien> findByEntretienId(Long entretienId);
    boolean existsByEntretienIdAndEmployeId(Long entretienId, Long employeId);
}
