package SI.RH.repository;

import SI.RH.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SoldeToutCompteRepository extends JpaRepository<SoldeToutCompte, Long> {
    Optional<SoldeToutCompte> findByRuptureId(Long ruptureId);
}
