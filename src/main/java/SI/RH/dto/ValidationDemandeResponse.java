package SI.RH.dto;

import SI.RH.entity.enums.DecisionValidation;
import SI.RH.entity.enums.NiveauValidation;

import java.time.LocalDateTime;

public record ValidationDemandeResponse(
    Integer id,
    Integer employeValideurId,
    NiveauValidation niveau,
    DecisionValidation decision,
    String commentaire,
    LocalDateTime dateDecision
) {}
