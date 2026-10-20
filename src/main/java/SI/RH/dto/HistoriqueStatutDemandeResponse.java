package SI.RH.dto;

import SI.RH.entity.enums.StatutDemande;

import java.time.LocalDateTime;

public record HistoriqueStatutDemandeResponse(
    Integer id,
    StatutDemande ancienStatut,
    StatutDemande nouveauStatut,
    Integer employeActeurId,
    String commentaire,
    LocalDateTime dateTransition
) {}
