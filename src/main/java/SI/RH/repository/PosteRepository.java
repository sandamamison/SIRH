package SI.RH.repository;

import SI.RH.entity.Poste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosteRepository extends JpaRepository<Poste, Integer> {
    List<Poste> findByDepartementId(Integer departementId);
    boolean existsByCodePoste(String codePoste);
}
