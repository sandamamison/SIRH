package SI.RH.entity.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Statuts possibles d'une demande de recrutement et règles de transition.
 */
public enum StatutDemande {
    BROUILLON,
    SOUMISE,
    VALIDEE_RH,
    VALIDEE_DIRECTION,
    RECRUTEMENT_OUVERT,
    CLOTUREE,
    REJETEE;

    public Set<StatutDemande> suivantsPossibles() {
        return switch (this) {
            case BROUILLON          -> EnumSet.of(SOUMISE);
            case SOUMISE            -> EnumSet.of(VALIDEE_RH, REJETEE);
            case VALIDEE_RH         -> EnumSet.of(VALIDEE_DIRECTION, REJETEE);
            case VALIDEE_DIRECTION  -> EnumSet.of(RECRUTEMENT_OUVERT);
            case RECRUTEMENT_OUVERT -> EnumSet.of(CLOTUREE);
            case CLOTUREE, REJETEE  -> EnumSet.noneOf(StatutDemande.class);
        };
    }

    public boolean peutPasserA(StatutDemande cible) {
        return suivantsPossibles().contains(cible);
    }

    /** RG1-07 (décision D2) : seule une demande en brouillon est modifiable. */
    public boolean estModifiable() {
        return this == BROUILLON;
    }
}
