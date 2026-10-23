package SI.RH.service.port;

import SI.RH.entity.Employe;

/**
 * Boundary to bloc 4; bloc 3 only publishes the initialisation request.
 */
public interface ContratAffectationPort {
    void demanderCreationInitiale(Employe employe, Long candidatureId);
}
