package SI.RH.repository;

import SI.RH.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeRepository extends JpaRepository<Employe, Integer> {
    boolean existsByMatricule(String matricule);
    boolean existsByEmail(String email);
    List<Employe> findByDepartementId(Integer departementId);
    List<Employe> findByServiceId(Integer serviceId);
    List<Employe> findBySectionId(Integer sectionId);
    boolean existsByDepartementId(Integer departementId);
    boolean existsByServiceId(Integer serviceId);
    boolean existsBySectionId(Integer sectionId);
    boolean existsByEmployeManagerId(Integer employeManagerId);
}
