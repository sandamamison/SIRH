package SI.RH.dto;

import SI.RH.entity.enums.StatutEmploye;

import java.time.LocalDate;

public class EmployeResponse {
    public final Long id, personneId;
    public final String matricule;
    public final LocalDate dateEmbauche;
    public final StatutEmploye statut;

    public EmployeResponse(Long i, String m, Long p, LocalDate d, StatutEmploye s) {
        id = i;
        matricule = m;
        personneId = p;
        dateEmbauche = d;
        statut = s;
    }
}
