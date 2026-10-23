package SI.RH.dto;

public record ServiceResponse(
    Integer id,
    String codeService,
    String nomService,
    String description,
    Integer departementId,
    String departementNom,
    Integer employeResponsableId
) {}
