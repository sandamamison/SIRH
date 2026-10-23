package SI.RH.dto;

import java.util.List;

public record DemandeDetailResponse(
    DemandeResponse demande,
    List<ValidationDemandeResponse> validations,
    List<HistoriqueStatutDemandeResponse> historique
) {}
