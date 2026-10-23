package SI.RH.dto;

import java.time.LocalDate;

public record EmployeResponse(
    Integer id,
    String matricule,
    String nom,
    String prenom,
    String email,
    String telephone,
    LocalDate dateEmbauche,
    boolean actif,
    Integer employeManagerId,
    Integer departementId,
    Integer serviceId,
    Integer sectionId,
    String uniteType,
    String uniteNom
) {}
