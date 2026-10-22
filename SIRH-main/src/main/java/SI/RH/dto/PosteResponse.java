package SI.RH.dto;

public record PosteResponse(
    Integer id,
    String codePoste,
    String titre,
    String descriptionMission,
    Integer salaireMin,
    Integer salaireMax,
    Integer departementId,
    String departementNom
) {}
