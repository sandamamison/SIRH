package SI.RH.entity.enums;

public enum StatutDemandeConge {
    BROUILLON,
    SOUMISE,
    VALIDEE_MANAGER,
    VALIDEE_RH,
    REFUSEE,
    ANNULEE;

    public boolean peutPasserA(StatutDemandeConge cible) {
        return switch (this) {
            case BROUILLON       -> cible == SOUMISE;
            case SOUMISE         -> cible == VALIDEE_MANAGER || cible == REFUSEE || cible == ANNULEE;
            case VALIDEE_MANAGER -> cible == VALIDEE_RH || cible == REFUSEE;
            case VALIDEE_RH      -> cible == ANNULEE;
            default              -> false;
        };
    }
}
