package SI.RH.repository;

import SI.RH.entity.BulletinPaie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BulletinPaieRepository extends JpaRepository<BulletinPaie, Long> {
    List<BulletinPaie> findByPeriodePaieId(Long periodePaieId);
    Optional<BulletinPaie> findByPeriodePaieIdAndEmployeId(Long periodePaieId, Long employeId);
    List<BulletinPaie> findByEmployeId(Long employeId);
}
