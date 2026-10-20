package SI.RH.service;

import SI.RH.dto.ActionDemandeRequest;
import SI.RH.dto.DemandeRequest;
import SI.RH.dto.DemandeResponse;
import SI.RH.entity.DemandeRecrutement;
import SI.RH.entity.HistoriqueStatutDemande;
import SI.RH.entity.Poste;
import SI.RH.entity.ValidationDemande;
import SI.RH.entity.enums.DecisionValidation;
import SI.RH.entity.enums.MotifRecrutement;
import SI.RH.entity.enums.NiveauValidation;
import SI.RH.entity.enums.StatutDemande;
import SI.RH.exception.BusinessException;
import SI.RH.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemandeRecrutementServiceTest {

    @Mock
    private DemandeRecrutementRepository demandeRepository;

    @Mock
    private PosteRepository posteRepository;

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private ValidationDemandeRepository validationRepository;

    @Mock
    private HistoriqueStatutDemandeRepository historiqueRepository;

    @InjectMocks
    private DemandeRecrutementService service;

    private Poste posteTest;

    @BeforeEach
    void setUp() {
        posteTest = new Poste();
        posteTest.setId(1);
        posteTest.setCodePoste("DEV-01");
        posteTest.setTitre("Developpeur Java");
        posteTest.setDepartementId(10);
    }

    @Test
    void creer_succes_creation() {
        DemandeRequest req = new DemandeRequest(
            1, MotifRecrutement.CREATION, "Nouveau besoin", null,
            2, 5000000L, LocalDate.now().plusMonths(1), 100
        );

        when(posteRepository.findById(1)).thenReturn(Optional.of(posteTest));
        when(demandeRepository.save(any(DemandeRecrutement.class))).thenAnswer(invocation -> {
            DemandeRecrutement d = invocation.getArgument(0);
            d.setId(10);
            return d;
        });

        DemandeResponse resp = service.creer(req);

        assertNotNull(resp);
        assertEquals(10, resp.id());
        assertEquals(StatutDemande.BROUILLON, resp.statut());
        assertEquals(MotifRecrutement.CREATION, resp.motif());

        verify(historiqueRepository).save(any(HistoriqueStatutDemande.class));
    }

    @Test
    void creer_echec_remplacementSansEmploye_RG1_03() {
        DemandeRequest req = new DemandeRequest(
            1, MotifRecrutement.REMPLACEMENT, "Depart retraite", null,
            1, 3000000L, LocalDate.now().plusMonths(1), 100
        );

        when(posteRepository.findById(1)).thenReturn(Optional.of(posteTest));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.creer(req));
        assertTrue(ex.getMessage().contains("RG1-03"));
        verify(demandeRepository, never()).save(any());
    }

    @Test
    void creer_echec_creationAvecEmploye_RG1_03() {
        DemandeRequest req = new DemandeRequest(
            1, MotifRecrutement.CREATION, "Creation", 999,
            1, 3000000L, LocalDate.now().plusMonths(1), 100
        );

        when(posteRepository.findById(1)).thenReturn(Optional.of(posteTest));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.creer(req));
        assertTrue(ex.getMessage().contains("RG1-03"));
        verify(demandeRepository, never()).save(any());
    }

    @Test
    void modifier_succes_enBrouillon() {
        DemandeRecrutement d = new DemandeRecrutement();
        d.setId(1);
        d.setStatut(StatutDemande.BROUILLON);
        d.setEmployeDemandeurId(100);
        d.setPosteId(1);

        DemandeRequest req = new DemandeRequest(
            1, MotifRecrutement.CREATION, "Justification modifiee", null,
            3, 6000000L, LocalDate.now().plusMonths(2), 100
        );

        when(demandeRepository.findById(1)).thenReturn(Optional.of(d));
        when(posteRepository.findById(1)).thenReturn(Optional.of(posteTest));
        when(demandeRepository.save(any(DemandeRecrutement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DemandeResponse resp = service.modifier(1, req);

        assertEquals("Justification modifiee", resp.justification());
        assertEquals(3, resp.effectifDemande());
    }

    @Test
    void modifier_echec_statutNonModifiable_RG1_07() {
        DemandeRecrutement d = new DemandeRecrutement();
        d.setId(1);
        d.setStatut(StatutDemande.SOUMISE);
        d.setEmployeDemandeurId(100);

        DemandeRequest req = new DemandeRequest(
            1, MotifRecrutement.CREATION, "Modif", null,
            1, 3000000L, LocalDate.now().plusMonths(1), 100
        );

        when(demandeRepository.findById(1)).thenReturn(Optional.of(d));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.modifier(1, req));
        assertTrue(ex.getMessage().contains("RG1-07"));
        verify(demandeRepository, never()).save(d);
    }

    @Test
    void modifier_echec_autreDemandeur() {
        DemandeRecrutement d = new DemandeRecrutement();
        d.setId(1);
        d.setStatut(StatutDemande.BROUILLON);
        d.setEmployeDemandeurId(100);

        DemandeRequest req = new DemandeRequest(
            1, MotifRecrutement.CREATION, "Modif", null,
            1, 3000000L, LocalDate.now().plusMonths(1), 999
        );

        when(demandeRepository.findById(1)).thenReturn(Optional.of(d));

        assertThrows(BusinessException.class, () -> service.modifier(1, req));
    }

    @Test
    void soumettre_succes() {
        DemandeRecrutement d = new DemandeRecrutement();
        d.setId(1);
        d.setStatut(StatutDemande.BROUILLON);
        d.setEmployeDemandeurId(100);
        d.setPosteId(1);

        when(demandeRepository.findById(1)).thenReturn(Optional.of(d));
        when(demandeRepository.save(any(DemandeRecrutement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActionDemandeRequest action = new ActionDemandeRequest(100, "Je soumets la demande");
        DemandeResponse resp = service.soumettre(1, action);

        assertEquals(StatutDemande.SOUMISE, resp.statut());
        verify(historiqueRepository).save(any(HistoriqueStatutDemande.class));
    }

    @Test
    void valider_succes_etapeRH_RG1_05_RG1_06() {
        DemandeRecrutement d = new DemandeRecrutement();
        d.setId(1);
        d.setStatut(StatutDemande.SOUMISE);
        d.setPosteId(1);

        when(demandeRepository.findById(1)).thenReturn(Optional.of(d));
        when(demandeRepository.save(any(DemandeRecrutement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActionDemandeRequest action = new ActionDemandeRequest(200, "Approuve par RH");
        DemandeResponse resp = service.valider(1, action);

        assertEquals(StatutDemande.VALIDEE_RH, resp.statut());

        ArgumentCaptor<ValidationDemande> valCaptor = ArgumentCaptor.forClass(ValidationDemande.class);
        verify(validationRepository).save(valCaptor.capture());
        assertEquals(NiveauValidation.RH, valCaptor.getValue().getNiveau());
        assertEquals(DecisionValidation.APPROUVE, valCaptor.getValue().getDecision());

        verify(historiqueRepository).save(any(HistoriqueStatutDemande.class));
    }

    @Test
    void valider_succes_etapeDirection_RG1_05_RG1_06() {
        DemandeRecrutement d = new DemandeRecrutement();
        d.setId(1);
        d.setStatut(StatutDemande.VALIDEE_RH);
        d.setPosteId(1);

        when(demandeRepository.findById(1)).thenReturn(Optional.of(d));
        when(demandeRepository.save(any(DemandeRecrutement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActionDemandeRequest action = new ActionDemandeRequest(300, "Approuve par Direction");
        DemandeResponse resp = service.valider(1, action);

        assertEquals(StatutDemande.VALIDEE_DIRECTION, resp.statut());

        ArgumentCaptor<ValidationDemande> valCaptor = ArgumentCaptor.forClass(ValidationDemande.class);
        verify(validationRepository).save(valCaptor.capture());
        assertEquals(NiveauValidation.DIRECTION, valCaptor.getValue().getNiveau());
        assertEquals(DecisionValidation.APPROUVE, valCaptor.getValue().getDecision());
    }

    @Test
    void valider_echec_statutBrouillon() {
        DemandeRecrutement d = new DemandeRecrutement();
        d.setId(1);
        d.setStatut(StatutDemande.BROUILLON);

        when(demandeRepository.findById(1)).thenReturn(Optional.of(d));

        ActionDemandeRequest action = new ActionDemandeRequest(200, "Validation impossible");
        assertThrows(BusinessException.class, () -> service.valider(1, action));
    }

    @Test
    void refuser_succes_avecCommentaire_D6() {
        DemandeRecrutement d = new DemandeRecrutement();
        d.setId(1);
        d.setStatut(StatutDemande.SOUMISE);
        d.setPosteId(1);

        when(demandeRepository.findById(1)).thenReturn(Optional.of(d));
        when(demandeRepository.save(any(DemandeRecrutement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActionDemandeRequest action = new ActionDemandeRequest(200, "Budget excessif pour ce poste");
        DemandeResponse resp = service.refuser(1, action);

        assertEquals(StatutDemande.REJETEE, resp.statut());

        ArgumentCaptor<ValidationDemande> valCaptor = ArgumentCaptor.forClass(ValidationDemande.class);
        verify(validationRepository).save(valCaptor.capture());
        assertEquals(DecisionValidation.REJETE, valCaptor.getValue().getDecision());
        assertEquals("Budget excessif pour ce poste", valCaptor.getValue().getCommentaire());
    }

    @Test
    void refuser_echec_sansCommentaire_D6() {
        ActionDemandeRequest action = new ActionDemandeRequest(200, "   ");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refuser(1, action));
        assertTrue(ex.getMessage().contains("commentaire est obligatoire"));
    }

    @Test
    void marquerRecrutementOuvert_succes_D8() {
        DemandeRecrutement d = new DemandeRecrutement();
        d.setId(1);
        d.setStatut(StatutDemande.VALIDEE_DIRECTION);

        when(demandeRepository.findById(1)).thenReturn(Optional.of(d));
        when(demandeRepository.save(any(DemandeRecrutement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.marquerRecrutementOuvert(1, 200);

        assertEquals(StatutDemande.RECRUTEMENT_OUVERT, d.getStatut());
        verify(historiqueRepository).save(any(HistoriqueStatutDemande.class));
    }
}
