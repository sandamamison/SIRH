package SI.RH.repository;

import SI.RH.entity.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoraireRepository extends JpaRepository<Horaire, Long> {
    Optional<Horaire> findByLibelle(String libelle);
}
