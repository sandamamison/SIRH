package SI.RH.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeRequest(
    @NotBlank(message = "Le matricule est obligatoire")
    @Size(max = 30, message = "Le matricule ne doit pas depasser 30 caracteres")
    String matricule,

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
    String nom,

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 100, message = "Le prenom ne doit pas depasser 100 caracteres")
    String prenom,

    @Email(message = "L'email doit etre valide")
    @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres")
    String email,

    @Size(max = 30, message = "Le telephone ne doit pas depasser 30 caracteres")
    String telephone,

    @NotNull(message = "La date d'embauche est obligatoire")
    LocalDate dateEmbauche,

    Boolean actif,

    Integer employeManagerId,

    // RG5-04 : exactement une des trois unites doit etre renseignee
    Integer departementId,
    Integer serviceId,
    Integer sectionId
) {}
