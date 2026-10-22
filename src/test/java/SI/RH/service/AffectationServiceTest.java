package SI.RH.service;

import SI.RH.dto.AffectationRequest;
import SI.RH.dto.AffectationResponse;
import SI.RH.entity.Affectation;
import SI.RH.entity.Poste;
import SI.RH.exception.BusinessException;
import SI.RH.repository.AffectationRepository;
import SI.RH.repository.PosteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AffectationServiceTest {

    @Mock
    private AffectationRepository affectationRepository;

    @Mock
    private PosteRepository posteRepository;

    @InjectMocks
    private AffectationService service;

    private Poste posteTest;

    @BeforeEach
    void setUp() {
        posteTest = new Poste();
        posteTest.setId(1);
        posteTest.setTitre("Developpeur");
    }

    @Test
    void creer_succes() {
        AffectationRequest req = new AffectationRequest(
            10, 1,
            LocalDate.now(),
            null
        );
        when(posteRepository.findById(1)).thenReturn(Optional.of(posteTest));
        when(affectationRepository.findByEmployeIdOrderByDateDebutDesc(10)).thenReturn(List.of());
        when(affectationRepository.save(any(Affectation.class))).thenAnswer(inv -> {
            Affectation a = inv.getArgument(0);
            a.setId(50);
            return a;
        });

        AffectationResponse resp = service.creer(req);

        assertEquals(50, resp.id());
        assertEquals("Developpeur", resp.posteTitre());
        assertTrue(resp.active());
    }

    @Test
    void creer_echec_chevauchement_RG4_04() {
        AffectationRequest req = new AffectationRequest(
            10, 1,
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2026, 6, 1)
        );

        Affectation existante = new Affectation();
        existante.setId(1);
        existante.setEmployeId(10);
        existante.setPosteId(1);
        existante.setDateDebut(LocalDate.of(2025, 1, 1));
        existante.setDateFin(LocalDate.of(2025, 12, 31));

        when(posteRepository.findById(1)).thenReturn(Optional.of(posteTest));
        when(affectationRepository.findByEmployeIdOrderByDateDebutDesc(10))
            .thenReturn(List.of(existante));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> service.creer(req));
        assertTrue(ex.getMessage().contains("RG4-04"));
        verify(affectationRepository, never()).save(any());
    }

    @Test
    void creer_echec_dateFinAvantDebut() {
        AffectationRequest req = new AffectationRequest(
            10, 1,
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2025, 1, 1)
        );
        when(posteRepository.findById(1)).thenReturn(Optional.of(posteTest));

        assertThrows(BusinessException.class, () -> service.creer(req));
    }

    @Test
    void estPosteVacant_false_quandAffectationActive() {
        when(affectationRepository.existsByPosteIdAndDateFinIsNull(1)).thenReturn(true);
        assertFalse(service.estPosteVacant(1));
    }

    @Test
    void estPosteVacant_true_quandAucuneAffectation() {
        when(affectationRepository.existsByPosteIdAndDateFinIsNull(1)).thenReturn(false);
        assertTrue(service.estPosteVacant(1));
    }
}