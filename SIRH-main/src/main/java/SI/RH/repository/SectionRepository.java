package SI.RH.repository;

import SI.RH.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Integer> {
    boolean existsByCodeSection(String codeSection);
    List<Section> findByServiceId(Integer serviceId);
    boolean existsByServiceId(Integer serviceId);
    boolean existsByEmployeResponsableId(Integer employeResponsableId);
}
