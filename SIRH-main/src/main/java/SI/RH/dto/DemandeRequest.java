package SI.RH.dto;

import SI.RH.entity.enums.MotifRecrutement;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record DemandeRequest(
    @NotNull(message = "Le poste est obligatoire")
    Integer posteId,

    @NotNull(message = "Le motif est obligatoire")
    MotifRecrutement motif,

    @NotBlank(message = "La justification est obligatoire")
    String justification,

    Integer employeRemplaceId,

    @Positive(message = "L'effectif demande doit etre superieur a 0")
    Integer effectifDemande,

    @NotNull(message = "Le budget prevu est obligatoire")
    @Positive(message = "Le budget prevu doit etre superieur a 0")
    Long budgetPrevu,

    @NotNull(message = "La date souhaitee est obligatoire")
    @FutureOrPresent(message = "La date souhaitee doit etre aujourd'hui ou dans le futur")
    LocalDate dateSouhaitee,

    @NotNull(message = "Le demandeur est obligatoire")
    Integer employeDemandeurId
) {}
