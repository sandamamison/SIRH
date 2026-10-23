package SI.RH.dto;

import jakarta.validation.constraints.NotNull;

public record ActionDemandeRequest(
    @NotNull(message = "L'employe acteur est obligatoire")
    Integer employeActeurId,

    String commentaire
) {}
