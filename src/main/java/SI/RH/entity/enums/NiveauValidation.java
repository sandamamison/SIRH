package SI.RH.entity.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * Niveaux d'approbation d'une demande de recrutement : RH puis DIRECTION.
 */
public enum NiveauValidation {
    RH(StatutDemande.SOUMISE, StatutDemande.VALIDEE_RH),
    DIRECTION(StatutDemande.VALIDEE_RH, StatutDemande.VALIDEE_DIRECTION);

    private final StatutDemande statutRequis;        // statut dans lequel ce niveau a la main
    private final StatutDemande statutApresApprobation;

    NiveauValidation(StatutDemande requis, StatutDemande apres) {
        this.statutRequis = requis;
        this.statutApresApprobation = apres;
    }

    public StatutDemande statutRequis() {
        return statutRequis;
    }

    public StatutDemande statutApresApprobation() {
        return statutApresApprobation;
    }

    /** RG1-05 : quel niveau doit décider pour une demande dans ce statut ? (vide si aucun) */
    public static Optional<NiveauValidation> pourStatut(StatutDemande statut) {
        return Arrays.stream(values()).filter(n -> n.statutRequis == statut).findFirst();
    }
}
