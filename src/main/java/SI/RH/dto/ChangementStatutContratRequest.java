package SI.RH.dto;

import SI.RH.entity.enums.StatutContrat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Requete generique pour un changement de statut de contrat (RG4-01 a RG4-03).
 * Le statut cible doit etre atteignable depuis le statut courant (peutPasserA).
 */
public record ChangementStatutContratRequest(
    @NotNull(message = "Le statut cible est obligatoire")
    StatutContrat statut,

    @NotNull(message = "L'employe acteur est obligatoire")
    Integer employeActeurId,

    // Renseigne uniquement lors du passage CREE -> SIGNE
    LocalDate dateSignature,

    String commentaire
) {}