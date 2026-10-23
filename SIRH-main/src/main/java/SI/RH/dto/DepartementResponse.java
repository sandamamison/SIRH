package SI.RH.dto;

public record DepartementResponse(
    Integer id,
    String codeDepartement,
    String nomDepartement,
    String description,
    Integer employeResponsableId,
    Integer departementParentId
) {}
