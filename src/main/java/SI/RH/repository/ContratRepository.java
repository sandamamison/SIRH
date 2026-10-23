package SI.RH.repository;

import SI.RH.entity.Contrat;
import SI.RH.entity.enums.StatutContrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContratRepository extends JpaRepository<Contrat, Integer> {

    List<Contrat> findByEmployeIdOrderByDateDebutDesc(Integer employeId);

    Optional<Contrat> findFirstByEmployeIdAndDateFinIsNullOrderByDateDebutDesc(Integer employeId);

    boolean existsByNumeroContrat(String numeroContrat);

    boolean existsByContratPrecedentId(Integer contratPrecedentId);

    boolean existsByEmployeIdAndStatutIn(Integer employeId, List<StatutContrat> statuts);

    /**
     * RG4-02 (double garde-fou applicatif en plus de la contrainte EXCLUDE en BDD).
     * Retourne true si un autre contrat de cet employe chevauche la periode demandee.
     */
    @Query("""
        SELECT COUNT(c) > 0 FROM Contrat c
        WHERE c.employeId = :employeId
          AND (:excludeId IS NULL OR c.id <> :excludeId)
          AND c.dateDebut <= :dateFin
          AND (c.dateFin IS NULL OR c.dateFin >= :dateDebut)
        """)
    boolean existsChevauchement(
        @Param("employeId") Integer employeId,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin,
        @Param("excludeId") Integer excludeId
    );

    List<Contrat> findByEmployeIdOrderByIdDesc(Integer employeId);
}