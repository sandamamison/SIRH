package SI.RH.entity.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Statuts possibles d'un contrat et regles de transition (Bloc 4).
 *
 * Workflow :
 *   CREE  -> SIGNE
 *   SIGNE -> RENOUVELE | NON_RENOUVELE | TERMINE | ROMPU
 *   Les etats finaux n'ont plus de transition.
 */
public enum StatutContrat {
    CREE,
    SIGNE,
    RENOUVELE,
    NON_RENOUVELE,
    TERMINE,
    ROMPU;

    public Set<StatutContrat> suivantsPossibles() {
        return switch (this) {
            case CREE  -> EnumSet.of(SIGNE);
            case SIGNE -> EnumSet.of(RENOUVELE, NON_RENOUVELE, TERMINE, ROMPU);
            case RENOUVELE, NON_RENOUVELE, TERMINE, ROMPU ->
                    EnumSet.noneOf(StatutContrat.class);
        };
    }

    public boolean peutPasserA(StatutContrat cible) {
        return suivantsPossibles().contains(cible);
    }

    public boolean estFinal() {
        return suivantsPossibles().isEmpty();
    }
}