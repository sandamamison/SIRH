package SI.RH.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AffectationRequest(
    @NotNull(message = "L'employe est obligatoire")
    Integer employeId,

    @NotNull(message = "Le poste est obligatoire")
    Integer posteId,

    @NotNull(message = "La date de debut est obligatoire")
    LocalDate dateDebut,

    LocalDate dateFin
) {}