package SI.RH.service;

import SI.RH.dto.EmployeRequest;
import SI.RH.dto.EmployeResponse;
import SI.RH.entity.Employe;
import SI.RH.entity.Section;
import SI.RH.exception.BusinessException;
import SI.RH.exception.NotFoundException;
import SI.RH.repository.DepartementRepository;
import SI.RH.repository.EmployeRepository;
import SI.RH.repository.SectionRepository;
import SI.RH.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {

    @Mock
    private EmployeRepository employeRepository;

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private EmployeService service;

    private EmployeRequest reqSection() {
        return new EmployeRequest(
            "EMP-0001", "Rakoto", "Naina", "rakoto.n@sirh.mg", "0340000001",
            LocalDate.now(), null, null, null, null, 5
        );
    }

    @Test
    void creer_succes_rattacheSection_RG5_04() {
        when(sectionRepository.existsById(5)).thenReturn(true);
        when(employeRepository.save(any(Employe.class))).thenAnswer(invocation -> {
            Employe e = invocation.getArgument(0);
            e.setId(1);
            return e;
        });
        Section sec = new Section();
        sec.setNomSection("Backend");
        when(sectionRepository.findById(5)).thenReturn(Optional.of(sec));

        EmployeResponse resp = service.creer(reqSection());

        assertEquals(1, resp.id());
        assertTrue(resp.actif());
        assertEquals("SECTION", resp.uniteType());
        assertEquals("Backend", resp.uniteNom());
    }

    @Test
    void creer_echec_aucuneUnite_RG5_04() {
        EmployeRequest req = new EmployeRequest(
            "EMP-0001", "Rakoto", "Naina", null, null,
            LocalDate.now(), null, null, null, null, null
        );

        BusinessException ex = assertThrows(BusinessException.class, () -> service.creer(req));
        assertTrue(ex.getMessage().contains("RG5-04"));
        verify(employeRepository, never()).save(any());
    }

    @Test
    void creer_echec_plusieursUnites_RG5_04() {
        EmployeRequest req = new EmployeRequest(
            "EMP-0001", "Rakoto", "Naina", null, null,
            LocalDate.now(), null, null, 1, 2, null
        );

        BusinessException ex = assertThrows(BusinessException.class, () -> service.creer(req));
        assertTrue(ex.getMessage().contains("RG5-04"));
        verify(employeRepository, never()).save(any());
    }

    @Test
    void creer_echec_matriculeExistant_RG5_05() {
        when(employeRepository.existsByMatricule("EMP-0001")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.creer(reqSection()));
        assertTrue(ex.getMessage().contains("matricule"));
        verify(employeRepository, never()).save(any());
    }

    @Test
    void creer_echec_sectionInexistante() {
        when(sectionRepository.existsById(5)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.creer(reqSection()));
        verify(employeRepository, never()).save(any());
    }

    @Test
    void modifier_echec_managerSoiMeme_RG5_06() {
        Employe e = new Employe();
        e.setId(1);
        e.setMatricule("EMP-0001");
        when(employeRepository.findById(1)).thenReturn(Optional.of(e));
        when(departementRepository.existsById(10)).thenReturn(true);

        EmployeRequest req = new EmployeRequest(
            "EMP-0001", "Rakoto", "Naina", null, null,
            LocalDate.now(), true, 1, 10, null, null
        );

        BusinessException ex = assertThrows(BusinessException.class, () -> service.modifier(1, req));
        assertTrue(ex.getMessage().contains("RG5-06"));
        verify(employeRepository, never()).save(any());
    }

    @Test
    void supprimer_echec_avecSubordonnes_RG5_07() {
        when(employeRepository.existsById(1)).thenReturn(true);
        when(employeRepository.existsByEmployeManagerId(1)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.supprimer(1));
        assertTrue(ex.getMessage().contains("RG5-07"));
        verify(employeRepository, never()).deleteById(any());
    }

    @Test
    void lister_echec_plusieursFiltres() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> service.lister(1, 2, null));
        assertTrue(ex.getMessage().contains("Un seul filtre"));
    }
}
