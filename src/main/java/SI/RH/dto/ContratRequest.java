package SI.RH.dto;

import SI.RH.entity.enums.TypeContrat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ContratRequest(
    @NotNull(message = "L'employe est obligatoire")
    Integer employeId,

    @NotNull(message = "Le type de contrat est obligatoire")
    TypeContrat typeContrat,

    @NotNull(message = "La date de debut est obligatoire")
    LocalDate dateDebut,

    LocalDate dateFin, // null si CDI

    @NotNull(message = "Le salaire brut est obligatoire")
    @Min(value = 1, message = "Le salaire brut doit etre > 0")
    Integer salaireBrut,

    @NotNull(message = "L'employe createur est obligatoire")
    Integer employeCreateurId
) {}