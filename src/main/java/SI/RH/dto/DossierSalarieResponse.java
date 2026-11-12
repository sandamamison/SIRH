package SI.RH.dto;

import SI.RH.entity.Document;
import SI.RH.entity.Employe;
import SI.RH.entity.Onboarding;
import SI.RH.entity.Personne;

import java.util.List;

public record DossierSalarieResponse(
        Employe employe,
        Personne personne,
        List<ContratResponse> contrats,
        List<AffectationResponse> affectations,
        List<Document> documents,
        List<Onboarding> onboardings
) {}
