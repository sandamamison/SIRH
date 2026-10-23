package SI.RH.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PosteRequest(
    @NotBlank(message = "Le code poste est obligatoire")
    @Size(max = 30, message = "Le code poste ne doit pas depasser 30 caracteres")
    String codePoste,

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 100, message = "Le titre ne doit pas depasser 100 caracteres")
    String titre,

    String descriptionMission,

    @PositiveOrZero(message = "Le salaire minimum doit etre positif ou nul")
    Integer salaireMin,

    @PositiveOrZero(message = "Le salaire maximum doit etre positif ou nul")
    Integer salaireMax,

    @NotNull(message = "Le departement est obligatoire")
    Integer departementId
) {}
