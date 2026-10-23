package SI.RH.service.port;

import SI.RH.entity.Employe;
import org.springframework.stereotype.Component;

@Component
public class NoOpContratAffectationPort implements ContratAffectationPort {
    public void demanderCreationInitiale(Employe e, Long c) {
    }
}
