package SI.RH.entity;

import SI.RH.entity.enums.StatutContrat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatutContratTest {

    @Test
    void cree_peutPasserA_signe() {
        assertTrue(StatutContrat.CREE.peutPasserA(StatutContrat.SIGNE));
        assertFalse(StatutContrat.CREE.peutPasserA(StatutContrat.RENOUVELE));
        assertFalse(StatutContrat.CREE.peutPasserA(StatutContrat.TERMINE));
    }

    @Test
    void signe_peutPasserA_etatsTerminaux() {
        assertTrue(StatutContrat.SIGNE.peutPasserA(StatutContrat.RENOUVELE));
        assertTrue(StatutContrat.SIGNE.peutPasserA(StatutContrat.NON_RENOUVELE));
        assertTrue(StatutContrat.SIGNE.peutPasserA(StatutContrat.TERMINE));
        assertTrue(StatutContrat.SIGNE.peutPasserA(StatutContrat.ROMPU));
    }

    @Test
    void etatsFinaux_nOntPasDeSuivants() {
        assertTrue(StatutContrat.RENOUVELE.estFinal());
        assertTrue(StatutContrat.NON_RENOUVELE.estFinal());
        assertTrue(StatutContrat.TERMINE.estFinal());
        assertTrue(StatutContrat.ROMPU.estFinal());
        assertFalse(StatutContrat.SIGNE.estFinal());
    }
}