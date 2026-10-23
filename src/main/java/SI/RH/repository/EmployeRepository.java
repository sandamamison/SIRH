package SI.RH.repository;

import SI.RH.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {

    Optional<Employe> findByMatricule(String matricule);

    Optional<Employe> findByPersonneId(Long personneId);
}
