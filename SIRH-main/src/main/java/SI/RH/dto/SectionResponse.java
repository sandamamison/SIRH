package SI.RH.dto;

public record SectionResponse(
    Integer id,
    String codeSection,
    String nomSection,
    String description,
    Integer serviceId,
    String serviceNom,
    Integer employeResponsableId
) {}
