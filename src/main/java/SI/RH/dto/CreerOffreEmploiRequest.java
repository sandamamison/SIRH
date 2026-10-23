package SI.RH.dto;

import SI.RH.entity.OffreEmploi;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CreerOffreEmploiRequest(
    @NotNull(message = "L'offre d'emploi est obligatoire")
    @Valid
    OffreEmploi offre,

    @NotNull(message = "L'action est obligatoire")
    @Valid
    ActionDemandeRequest action
) {}
