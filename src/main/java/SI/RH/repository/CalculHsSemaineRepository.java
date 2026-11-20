package SI.RH.repository;

import SI.RH.entity.CalculHsSemaine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalculHsSemaineRepository extends JpaRepository<CalculHsSemaine, Long> {
    List<CalculHsSemaine> findByBulletinPaieIdOrderByNumeroSemaineAsc(Long bulletinPaieId);
    void deleteByBulletinPaieId(Long bulletinPaieId);
}
