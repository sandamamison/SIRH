package SI.RH.dto;

import SI.RH.entity.enums.MotifRecrutement;
import SI.RH.entity.enums.StatutDemande;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DemandeResponse(
    Integer id,
    Integer posteId,
    String posteTitre,
    Integer departementId,
    String departementNom,
    MotifRecrutement motif,
    String justification,
    Integer employeRemplaceId,
    int effectifDemande,
    long budgetPrevu,
    LocalDate dateSouhaitee,
    StatutDemande statut,
    Integer employeDemandeurId,
    LocalDateTime dateCreation
) {}
