package SI.RH.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartementRequest(
    @NotBlank(message = "Le code departement est obligatoire")
    @Size(max = 20, message = "Le code departement ne doit pas depasser 20 caracteres")
    String codeDepartement,

    @NotBlank(message = "Le nom departement est obligatoire")
    @Size(max = 100, message = "Le nom departement ne doit pas depasser 100 caracteres")
    String nomDepartement,

    String description,

    Integer employeResponsableId,

    Integer departementParentId
) {}
