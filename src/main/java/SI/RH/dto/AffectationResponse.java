package SI.RH.dto;

import java.time.LocalDate;

public record AffectationResponse(
    Integer id,
    Integer employeId,
    Integer posteId,
    String posteTitre,
    LocalDate dateDebut,
    LocalDate dateFin,
    boolean active
) {}