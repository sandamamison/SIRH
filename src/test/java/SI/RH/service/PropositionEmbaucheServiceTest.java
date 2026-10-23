package SI.RH.service;

import SI.RH.entity.Candidature;
import SI.RH.entity.Employe;
import SI.RH.entity.Personne;
import SI.RH.entity.PropositionEmbauche;
import SI.RH.entity.enums.StatutCandidature;
import SI.RH.entity.enums.StatutEmploye;
import SI.RH.entity.enums.StatutProposition;
import SI.RH.exception.BusinessRuleException;
import SI.RH.repository.CandidatureRepository;
import SI.RH.repository.EmployeRepository;
import SI.RH.repository.HistoriquePropositionRepository;
import SI.RH.repository.PersonneRepository;
import SI.RH.repository.PropositionEmbaucheRepository;
import SI.RH.service.port.ContratAffectationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropositionEmbaucheServiceTest {

    @Mock PropositionEmbaucheRepository propositions;
    @Mock CandidatureRepository candidatures;
    @Mock PersonneRepository personnes;
    @Mock EmployeRepository employes;
    @Mock HistoriquePropositionRepository historique;
    @Mock ContratAffectationPort integration;

    PropositionEmbaucheService service;

    @BeforeEach
    void setUp() {
        service = new PropositionEmbaucheService(
                propositions, candidatures, personnes, employes, historique, integration);
    }

    @Test
    void refusesSendingForNonRetainedCandidature() {
        Candidature candidature = candidature(10L, StatutCandidature.EN_COURS);
        when(candidatures.findById(10L)).thenReturn(Optional.of(candidature));

        assertThrows(BusinessRuleException.class,
                () -> service.envoyer(10L, null, null, "rh"));
        verify(propositions, never()).save(any());
    }

    @Test
    void sendsAndAcceptsRetainedCandidature() {
        Personne person = person(20L);
        Candidature candidature = candidature(10L, StatutCandidature.RETENUE);
        PropositionEmbauche proposition = proposition(30L, 10L, StatutProposition.ENVOYEE);
        when(candidatures.findById(10L)).thenReturn(Optional.of(candidature));
        when(personnes.findById(20L)).thenReturn(Optional.of(person));
        when(propositions.findByCandidatureId(10L)).thenReturn(List.of());
        when(propositions.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(propositions.findById(30L)).thenReturn(Optional.of(proposition));
        when(employes.findByPersonneId(20L)).thenReturn(Optional.empty());
        when(employes.findByMatricule(any())).thenReturn(Optional.empty());
        when(employes.save(any())).thenAnswer(invocation -> {
            Employe employee = invocation.getArgument(0);
            employee.setId(40L);
            return employee;
        });

        PropositionEmbauche sent = service.envoyer(10L, null, "conditions", "rh");
        Employe employee = service.accepter(30L, "rh");

        assertEquals(StatutProposition.ENVOYEE, sent.getStatut());
        assertEquals(StatutEmploye.ACTIF, employee.getStatut());
        assertEquals(20L, employee.getPersonneId());
        assertTrue(employee.getMatricule().startsWith("EMP-"));
        verify(integration).demanderCreationInitiale(employee, 10L);
        verify(historique, times(2)).save(any());
    }

    @Test
    void rejectsAcceptanceWhenPersonAlreadyEmploye() {
        PropositionEmbauche proposition = proposition(30L, 10L, StatutProposition.ENVOYEE);
        when(propositions.findById(30L)).thenReturn(Optional.of(proposition));
        when(candidatures.findById(10L)).thenReturn(Optional.of(candidature(10L, StatutCandidature.RETENUE)));
        when(personnes.findById(20L)).thenReturn(Optional.of(person(20L)));
        when(employes.findByPersonneId(20L)).thenReturn(Optional.of(employee(40L, 20L)));

        assertThrows(BusinessRuleException.class, () -> service.accepter(30L, "rh"));
        verify(propositions, never()).save(any());
        verify(employes, never()).save(any());
    }

    @Test
    void rejectsAcceptanceOfFinalProposition() {
        PropositionEmbauche proposition = proposition(30L, 10L, StatutProposition.REFUSEE);
        when(propositions.findById(30L)).thenReturn(Optional.of(proposition));
        when(candidatures.findById(10L)).thenReturn(Optional.of(candidature(10L, StatutCandidature.RETENUE)));

        assertThrows(BusinessRuleException.class, () -> service.accepter(30L, "rh"));
        verify(employes, never()).save(any());
    }

    @Test
    void repeatedAcceptanceReturnsExistingEmployeeWithoutCreatingAnother() {
        PropositionEmbauche proposition = proposition(30L, 10L, StatutProposition.ACCEPTEE);
        Employe employee = employee(40L, 20L);
        when(propositions.findById(30L)).thenReturn(Optional.of(proposition));
        when(candidatures.findById(10L)).thenReturn(Optional.of(candidature(10L, StatutCandidature.RETENUE)));
        when(employes.findByPersonneId(20L)).thenReturn(Optional.of(employee));

        Employe result = service.accepter(30L, "rh");

        assertSame(employee, result);
        verify(employes, never()).save(any());
        verify(integration, never()).demanderCreationInitiale(any(), any());
    }

    private Candidature candidature(Long id, StatutCandidature status) {
        Candidature candidature = new Candidature();
        candidature.setId(id);
        candidature.setPersonneId(20L);
        candidature.setStatut(status);
        return candidature;
    }

    private Personne person(Long id) {
        Personne person = new Personne();
        person.setId(id);
        person.setNom("Doe");
        person.setPrenom("Jane");
        person.setEmail("jane-" + id + "@example.test");
        return person;
    }

    private PropositionEmbauche proposition(Long id, Long candidatureId, StatutProposition status) {
        PropositionEmbauche proposition = new PropositionEmbauche();
        proposition.setId(id);
        proposition.setCandidatureId(candidatureId);
        proposition.setStatut(status);
        return proposition;
    }

    private Employe employee(Long id, Long personId) {
        Employe employee = new Employe();
        employee.setId(id);
        employee.setPersonneId(personId);
        employee.setMatricule("EMP-EXISTING");
        employee.setStatut(StatutEmploye.ACTIF);
        return employee;
    }
}
