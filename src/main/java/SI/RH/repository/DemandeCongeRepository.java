package SI.RH.repository;

import SI.RH.entity.DemandeConge;
import SI.RH.entity.enums.StatutDemandeConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DemandeCongeRepository extends JpaRepository<DemandeConge, Long> {
    List<DemandeConge> findByEmployeId(Long employeId);
    List<DemandeConge> findByStatut(StatutDemandeConge statut);

    @Query("""
        SELECT COUNT(d) > 0 FROM DemandeConge d
        WHERE d.employeId = :employeId
          AND d.statut NOT IN ('REFUSEE', 'ANNULEE')
          AND d.dateDebut <= :fin
          AND d.dateFin >= :debut
          AND (:exclureId IS NULL OR d.id != :exclureId)
    """)
    boolean existsChevauchement(
            @Param("employeId") Long employeId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin,
            @Param("exclureId") Long exclureId
    );
}
