package SI.RH.service;

import SI.RH.dto.ContratRequest;
import SI.RH.dto.ContratResponse;
import SI.RH.entity.Contrat;
import SI.RH.entity.enums.StatutContrat;
import SI.RH.entity.enums.TypeContrat;
import SI.RH.exception.BusinessException;
import SI.RH.repository.ContratRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratServiceTest {

    @Mock
    private ContratRepository contratRepository;

    @InjectMocks
    private ContratService service;

    private Contrat contratSigne;

    @BeforeEach
    void setUp() {
        contratSigne = new Contrat();
        contratSigne.setId(1);
        contratSigne.setEmployeId(10);
        contratSigne.setTypeContrat(TypeContrat.CDD);
        contratSigne.setDateDebut(LocalDate.of(2025, 1, 1));
        contratSigne.setDateFin(LocalDate.of(2025, 12, 31));
        contratSigne.setSalaireBrut(3000000);
        contratSigne.setStatut(StatutContrat.SIGNE);
        contratSigne.setNumeroContrat("CTR-10-20250101");
        contratSigne.setEmployeCreateurId(100);
    }

    // --- RG4-01 : coherence type <-> dateFin ---

    @Test
    void creer_echec_cdiAvecDateFin_RG4_01() {
        ContratRequest req = new ContratRequest(
            10, TypeContrat.CDI,
            LocalDate.now(), LocalDate.now().plusYears(1),
            3000000, 100
        );
        BusinessException ex = assertThrows(BusinessException.class, () -> service.creer(req));
        assertTrue(ex.getMessage().contains("RG4-01"));
        verify(contratRepository, never()).save(any());
    }

    @Test
    void creer_echec_cddSansDateFin_RG4_01() {
        ContratRequest req = new ContratRequest(
            10, TypeContrat.CDD,
            LocalDate.now(), null,
            3000000, 100
        );
        BusinessException ex = assertThrows(BusinessException.class, () -> service.creer(req));
        assertTrue(ex.getMessage().contains("RG4-01"));
        verify(contratRepository, never()).save(any());
    }

    // --- RG4-02 : non-chevauchement ---

    @Test
    void creer_echec_chevauchement_RG4_02() {
        ContratRequest req = new ContratRequest(
            10, TypeContrat.CDD,
            LocalDate.of(2025, 6, 1), LocalDate.of(2026, 6, 1),
            3000000, 100
        );
        when(contratRepository.existsChevauchement(
            eq(10), any(LocalDate.class), any(LocalDate.class), isNull()))
            .thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.creer(req));
        assertTrue(ex.getMessage().contains("RG4-02"));
        verify(contratRepository, never()).save(any());
    }

    @Test
    void creer_succes() {
        ContratRequest req = new ContratRequest(
            10, TypeContrat.CDI,
            LocalDate.now(), null,
            3000000, 100
        );
        when(contratRepository.existsChevauchement(any(), any(), any(), any())).thenReturn(false);
        when(contratRepository.existsByNumeroContrat(anyString())).thenReturn(false);
        when(contratRepository.save(any(Contrat.class))).thenAnswer(inv -> {
            Contrat c = inv.getArgument(0);
            c.setId(42);
            return c;
        });

        ContratResponse resp = service.creer(req);

        assertNotNull(resp);
        assertEquals(StatutContrat.CREE, resp.statut());
        assertEquals(42, resp.id());
    }

    // --- RG4-03 : renouvellement ---

    @Test
    void renouveler_echec_dejaRenouvele_RG4_03() {
        when(contratRepository.findById(1)).thenReturn(Optional.of(contratSigne));
        when(contratRepository.existsByContratPrecedentId(1)).thenReturn(true);

        ContratRequest req = new ContratRequest(
            10, TypeContrat.CDD,
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
            3200000, 100
        );

        BusinessException ex = assertThrows(BusinessException.class,
            () -> service.renouveler(1, req));
        assertTrue(ex.getMessage().contains("RG4-03"));
    }

    @Test
    void renouveler_echec_statutNonSigne() {
        Contrat brouillon = new Contrat();
        brouillon.setId(2);
        brouillon.setEmployeId(10);
        brouillon.setStatut(StatutContrat.CREE);

        when(contratRepository.findById(2)).thenReturn(Optional.of(brouillon));
        when(contratRepository.existsByContratPrecedentId(2)).thenReturn(false);

        ContratRequest req = new ContratRequest(
            10, TypeContrat.CDI,
            LocalDate.of(2026, 1, 1), null,
            3500000, 100
        );

        assertThrows(BusinessException.class, () -> service.renouveler(2, req));
    }

    @Test
    void renouveler_echec_dateDebutAvantFinPrecedent_RG4_03() {
        when(contratRepository.findById(1)).thenReturn(Optional.of(contratSigne));
        when(contratRepository.existsByContratPrecedentId(1)).thenReturn(false);

        ContratRequest req = new ContratRequest(
            10, TypeContrat.CDD,
            LocalDate.of(2025, 6, 1), LocalDate.of(2026, 6, 1),
            3200000, 100
        );

        BusinessException ex = assertThrows(BusinessException.class,
            () -> service.renouveler(1, req));
        assertTrue(ex.getMessage().contains("RG4-03"));
    }

    @Test
    void renouveler_succes_RG4_03() {
        when(contratRepository.findById(1)).thenReturn(Optional.of(contratSigne));
        when(contratRepository.existsByContratPrecedentId(1)).thenReturn(false);
        when(contratRepository.existsChevauchement(any(), any(), any(), any())).thenReturn(false);
        when(contratRepository.existsByNumeroContrat(anyString())).thenReturn(false);
        when(contratRepository.save(any(Contrat.class))).thenAnswer(inv -> {
            Contrat c = inv.getArgument(0);
            if (c.getId() == null) c.setId(99);
            return c;
        });

        ContratRequest req = new ContratRequest(
            10, TypeContrat.CDI,
            LocalDate.of(2026, 1, 1), null,
            3500000, 100
        );

        ContratResponse resp = service.renouveler(1, req);

        assertEquals(99, resp.id());
        assertEquals(1, resp.contratPrecedentId());
        assertEquals(StatutContrat.RENOUVELE, contratSigne.getStatut());
    }

    // --- Signature ---

    @Test
    void signer_succes() {
        Contrat c = new Contrat();
        c.setId(5);
        c.setStatut(StatutContrat.CREE);
        when(contratRepository.findById(5)).thenReturn(Optional.of(c));
        when(contratRepository.save(any(Contrat.class))).thenReturn(c);

        ContratResponse resp = service.signer(5, 200);

        assertEquals(StatutContrat.SIGNE, resp.statut());
        assertEquals(200, resp.employeSignataireId());
        assertNotNull(resp.dateSignature());
    }

    @Test
    void signer_echec_dejaSigne() {
        when(contratRepository.findById(1)).thenReturn(Optional.of(contratSigne));

        assertThrows(BusinessException.class, () -> service.signer(1, 200));
    }
}