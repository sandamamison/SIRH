package SI.RH.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ServiceRequest(
    @NotBlank(message = "Le code service est obligatoire")
    @Size(max = 20, message = "Le code service ne doit pas depasser 20 caracteres")
    String codeService,

    @NotBlank(message = "Le nom service est obligatoire")
    @Size(max = 100, message = "Le nom service ne doit pas depasser 100 caracteres")
    String nomService,

    String description,

    @NotNull(message = "Le departement est obligatoire")
    Integer departementId,

    Integer employeResponsableId
) {}
