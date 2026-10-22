package SI.RH.entity.enums;

/**
 * Types de contrats possibles (Bloc 4).
 * RG4-01 : CE et CDD ont une date de fin, CDI n'en a pas.
 */
public enum TypeContrat {
    CE,   // Contrat d'Essai (6 mois, 1 renouvellement max)
    CDD,  // Contrat a Duree Determinee (2 ans max)
    CDI;  // Contrat a Duree Indeterminee

    public boolean exigeDateFin() {
        return this != CDI;
    }
}