package SI.RH.entity.enums;

public enum StatutCandidature {
    RECUE,
    EN_COURS,
    PRESELECTIONNEE,
    EN_ENTRETIEN,
    RETENUE,
    REJETEE;
    

    public boolean peutPasserA(StatutCandidature nouveauStatut) {
        if (nouveauStatut == null || nouveauStatut == this) {
            return false;
        }

        return switch (this) {
            case RECUE ->
                nouveauStatut == PRESELECTIONNEE
                || nouveauStatut == REJETEE;

            case PRESELECTIONNEE ->
                nouveauStatut == EN_ENTRETIEN
                || nouveauStatut == REJETEE;

            case EN_ENTRETIEN ->
                nouveauStatut == RETENUE
                || nouveauStatut == REJETEE;

            case EN_COURS, RETENUE, REJETEE -> false;
        };
    }
}