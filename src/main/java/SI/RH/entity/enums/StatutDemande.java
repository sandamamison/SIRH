package SI.RH.entity.enums;

/**
 * Workflow d'une demande de recrutement :
 * BROUILLON → SOUMISE → VALIDEE_RH → VALIDEE_DIRECTION → RECRUTEMENT_OUVERT → CLOTUREE
 *                                                                             ↘ REJETEE
 */
public enum StatutDemande {
    BROUILLON,
    SOUMISE,
    VALIDEE_RH,
    VALIDEE_DIRECTION,
    RECRUTEMENT_OUVERT,
    CLOTUREE,
    REJETEE
}
