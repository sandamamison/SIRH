package SI.RH.dto;

import SI.RH.entity.enums.StatutProposition;

import java.time.*;

public class PropositionResponse {
    public final Long id, candidatureId;
    public final StatutProposition statut;
    public final LocalDateTime dateEnvoi, dateReponse;
    public final LocalDate dateExpiration;
    public final String commentaire, createdBy;

    public PropositionResponse(Long i, Long c, StatutProposition s, LocalDateTime e, LocalDateTime r, LocalDate x, String m, String b) {
        id = i;
        candidatureId = c;
        statut = s;
        dateEnvoi = e;
        dateReponse = r;
        dateExpiration = x;
        commentaire = m;
        createdBy = b;
    }
}
