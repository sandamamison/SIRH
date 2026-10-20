package SI.RH.entity;

import SI.RH.entity.enums.StatutDemande;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatutDemandeTest {

    @Test
    void testTransitionsDepuisBrouillon() {
        assertTrue(StatutDemande.BROUILLON.peutPasserA(StatutDemande.SOUMISE));
        assertFalse(StatutDemande.BROUILLON.peutPasserA(StatutDemande.VALIDEE_RH));
        assertFalse(StatutDemande.BROUILLON.peutPasserA(StatutDemande.VALIDEE_DIRECTION));
        assertFalse(StatutDemande.BROUILLON.peutPasserA(StatutDemande.REJETEE));
    }

    @Test
    void testTransitionsDepuisSoumise() {
        assertTrue(StatutDemande.SOUMISE.peutPasserA(StatutDemande.VALIDEE_RH));
        assertTrue(StatutDemande.SOUMISE.peutPasserA(StatutDemande.REJETEE));
        assertFalse(StatutDemande.SOUMISE.peutPasserA(StatutDemande.VALIDEE_DIRECTION));
        assertFalse(StatutDemande.SOUMISE.peutPasserA(StatutDemande.BROUILLON));
    }

    @Test
    void testTransitionsDepuisValideeRh() {
        assertTrue(StatutDemande.VALIDEE_RH.peutPasserA(StatutDemande.VALIDEE_DIRECTION));
        assertTrue(StatutDemande.VALIDEE_RH.peutPasserA(StatutDemande.REJETEE));
        assertFalse(StatutDemande.VALIDEE_RH.peutPasserA(StatutDemande.SOUMISE));
        assertFalse(StatutDemande.VALIDEE_RH.peutPasserA(StatutDemande.RECRUTEMENT_OUVERT));
    }

    @Test
    void testTransitionsDepuisValideeDirection() {
        assertTrue(StatutDemande.VALIDEE_DIRECTION.peutPasserA(StatutDemande.RECRUTEMENT_OUVERT));
        assertFalse(StatutDemande.VALIDEE_DIRECTION.peutPasserA(StatutDemande.SOUMISE));
        assertFalse(StatutDemande.VALIDEE_DIRECTION.peutPasserA(StatutDemande.CLOTUREE));
    }

    @Test
    void testTransitionsDepuisRecrutementOuvert() {
        assertTrue(StatutDemande.RECRUTEMENT_OUVERT.peutPasserA(StatutDemande.CLOTUREE));
        assertFalse(StatutDemande.RECRUTEMENT_OUVERT.peutPasserA(StatutDemande.BROUILLON));
    }

    @Test
    void testEtatsFinauxSansTransitions() {
        assertTrue(StatutDemande.CLOTUREE.suivantsPossibles().isEmpty());
        assertTrue(StatutDemande.REJETEE.suivantsPossibles().isEmpty());
    }

    @Test
    void testModifiabiliteSelonRG1_07() {
        assertTrue(StatutDemande.BROUILLON.estModifiable());
        assertFalse(StatutDemande.SOUMISE.estModifiable());
        assertFalse(StatutDemande.VALIDEE_RH.estModifiable());
        assertFalse(StatutDemande.VALIDEE_DIRECTION.estModifiable());
        assertFalse(StatutDemande.RECRUTEMENT_OUVERT.estModifiable());
        assertFalse(StatutDemande.CLOTUREE.estModifiable());
        assertFalse(StatutDemande.REJETEE.estModifiable());
    }
}
