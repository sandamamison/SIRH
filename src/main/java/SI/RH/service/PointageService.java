package SI.RH.service;

import SI.RH.entity.AnomaliePointage;
import SI.RH.entity.Pointage;
import SI.RH.entity.enums.DirectionPointage;
import SI.RH.entity.enums.OriginePointage;
import SI.RH.entity.enums.StatutAnomalie;
import SI.RH.entity.enums.TypeAnomalie;
import SI.RH.repository.AnomaliePointageRepository;
import SI.RH.repository.PointageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PointageService {

    private final PointageRepository pointageRepository;
    private final AnomaliePointageRepository anomaliePointageRepository;

    public PointageService(PointageRepository pointageRepository,
                           AnomaliePointageRepository anomaliePointageRepository) {
        this.pointageRepository = pointageRepository;
        this.anomaliePointageRepository = anomaliePointageRepository;
    }

    /**
     * RG6-02 : Enregistrement d'un pointage avec détection automatique d'anomalie si cohérence rompue.
     */
    public Pointage pointer(Long employeId, DirectionPointage direction, OriginePointage origine) {
        LocalDateTime now = LocalDateTime.now();

        // Vérifier le dernier pointage de la même journée
        Optional<Pointage> dernier = pointageRepository.findFirstByEmployeIdOrderByHorodatageDesc(employeId);

        Pointage p = new Pointage();
        p.setEmployeId(employeId);
        p.setDirection(direction);
        p.setHorodatage(now);
        p.setOrigine(origine != null ? origine : OriginePointage.BADGE);
        p = pointageRepository.save(p);

        // RG6-04 : Détection d'anomalie (ex: 2 entrées consécutives sans sortie)
        if (dernier.isPresent()) {
            Pointage dp = dernier.get();
            if (dp.getHorodatage().toLocalDate().isEqual(now.toLocalDate()) && dp.getDirection() == direction) {
                AnomaliePointage anomalie = new AnomaliePointage();
                anomalie.setEmployeId(employeId);
                anomalie.setPointageId(p.getId());
                anomalie.setTypeAnomalie(direction == DirectionPointage.ENTREE
                        ? TypeAnomalie.ENTREE_SANS_SORTIE
                        : TypeAnomalie.HORS_PLANNING);
                anomalie.setDescription("Deux pointages successifs dans la même direction : " + direction);
                anomalie.setStatut(StatutAnomalie.DETECTEE);
                anomaliePointageRepository.save(anomalie);
            }
        }

        return p;
    }

    /**
     * RG6-03 : Calcul du temps réellement travaillé sur une journée (en minutes).
     */
    @Transactional(readOnly = true)
    public long calculerTempsTravailleMinutes(Long employeId, LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.atTime(23, 59, 59);

        List<Pointage> pointages = pointageRepository
                .findByEmployeIdAndHorodatageBetweenOrderByHorodatageAsc(employeId, debut, fin);

        long totalMinutes = 0;
        Pointage derniereEntree = null;

        for (Pointage pt : pointages) {
            if (pt.getDirection() == DirectionPointage.ENTREE) {
                derniereEntree = pt;
            } else if (pt.getDirection() == DirectionPointage.SORTIE && derniereEntree != null) {
                totalMinutes += Duration.between(derniereEntree.getHorodatage(), pt.getHorodatage()).toMinutes();
                derniereEntree = null;
            }
        }

        return totalMinutes;
    }

    @Transactional(readOnly = true)
    public List<Pointage> getPointagesJournee(Long employeId, LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.atTime(23, 59, 59);
        return pointageRepository.findByEmployeIdAndHorodatageBetweenOrderByHorodatageAsc(employeId, debut, fin);
    }

    @Transactional(readOnly = true)
    public List<AnomaliePointage> getAnomalies(Long employeId) {
        return anomaliePointageRepository.findByEmployeId(employeId);
    }
}
