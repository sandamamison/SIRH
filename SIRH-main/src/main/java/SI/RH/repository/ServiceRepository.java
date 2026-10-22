package SI.RH.repository;

import SI.RH.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
    boolean existsByCodeService(String codeService);
    List<Service> findByDepartementId(Integer departementId);
    boolean existsByDepartementId(Integer departementId);
    boolean existsByEmployeResponsableId(Integer employeResponsableId);
}
