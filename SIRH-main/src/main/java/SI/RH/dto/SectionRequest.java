package SI.RH.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SectionRequest(
    @NotBlank(message = "Le code section est obligatoire")
    @Size(max = 20, message = "Le code section ne doit pas depasser 20 caracteres")
    String codeSection,

    @NotBlank(message = "Le nom section est obligatoire")
    @Size(max = 100, message = "Le nom section ne doit pas depasser 100 caracteres")
    String nomSection,

    String description,

    @NotNull(message = "Le service est obligatoire")
    Integer serviceId,

    Integer employeResponsableId
) {}
