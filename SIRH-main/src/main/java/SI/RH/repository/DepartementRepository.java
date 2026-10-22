package SI.RH.repository;

import SI.RH.entity.Departement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartementRepository extends JpaRepository<Departement, Integer> {
    boolean existsByCodeDepartement(String codeDepartement);
    boolean existsByEmployeResponsableId(Integer employeResponsableId);
}
