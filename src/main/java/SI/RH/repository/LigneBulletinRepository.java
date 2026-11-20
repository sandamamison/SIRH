package SI.RH.repository;

import SI.RH.entity.LigneBulletin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneBulletinRepository extends JpaRepository<LigneBulletin, Long> {
    List<LigneBulletin> findByBulletinPaieIdOrderByOrdreAsc(Long bulletinPaieId);
    void deleteByBulletinPaieId(Long bulletinPaieId);
}
