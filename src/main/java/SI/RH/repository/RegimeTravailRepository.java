package SI.RH.repository;

import SI.RH.entity.RegimeTravail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegimeTravailRepository extends JpaRepository<RegimeTravail, Long> {
    Optional<RegimeTravail> findByLibelle(String libelle);
}
