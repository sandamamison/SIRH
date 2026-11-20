package SI.RH.controller;

import SI.RH.dto.*;
import SI.RH.entity.PeriodePaie;
import SI.RH.service.ExcelExportService;
import SI.RH.service.PaieService;
import SI.RH.service.PdfExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paie")
public class PaieController {

    private final PaieService paieService;
    private final PdfExportService pdfExportService;
    private final ExcelExportService excelExportService;

    public PaieController(PaieService paieService,
                          PdfExportService pdfExportService,
                          ExcelExportService excelExportService) {
        this.paieService = paieService;
        this.pdfExportService = pdfExportService;
        this.excelExportService = excelExportService;
    }

    @GetMapping("/periodes")
    public ResponseEntity<List<PeriodePaie>> getPeriodes() {
        return ResponseEntity.ok(paieService.getAllPeriodes());
    }

    @PostMapping("/periodes")
    public ResponseEntity<PeriodePaie> createPeriode(@RequestBody PeriodePaieRequest request) {
        return ResponseEntity.ok(paieService.createPeriode(request));
    }

    @PostMapping("/periodes/{periodeId}/generer")
    public ResponseEntity<List<BulletinPaieResponse>> genererPaie(@PathVariable Long periodeId) {
        return ResponseEntity.ok(paieService.genererPaiePeriode(periodeId));
    }

    @GetMapping("/periodes/{periodeId}/variables")
    public ResponseEntity<List<SI.RH.entity.VariablePaie>> getVariables(@PathVariable Long periodeId) {
        return ResponseEntity.ok(paieService.getVariables(periodeId));
    }

    @PostMapping("/periodes/{periodeId}/variables")
    public ResponseEntity<SI.RH.entity.VariablePaie> createVariable(
            @PathVariable Long periodeId,
            @RequestBody VariablePaieRequest request) {
        return ResponseEntity.ok(paieService.createVariable(periodeId, request));
    }

    @GetMapping("/periodes/{periodeId}/bulletins")
    public ResponseEntity<List<BulletinPaieResponse>> getBulletinsByPeriode(@PathVariable Long periodeId) {
        return ResponseEntity.ok(paieService.getBulletinsByPeriode(periodeId));
    }

    @GetMapping("/periodes/{periodeId}/etat")
    public ResponseEntity<List<EtatPaieDto>> getEtatPaie(@PathVariable Long periodeId) {
        return ResponseEntity.ok(paieService.getEtatPaie(periodeId));
    }

    @GetMapping("/bulletins/{id}")
    public ResponseEntity<BulletinPaieResponse> getBulletinById(@PathVariable Long id) {
        return ResponseEntity.ok(paieService.getBulletinResponseById(id));
    }

    @PutMapping("/bulletins/{id}/hs")
    public ResponseEntity<BulletinPaieResponse> updateHeuresSupplementaires(@PathVariable Long id, @RequestBody CalculHsRequest request) {
        return ResponseEntity.ok(paieService.updateHeuresSupplementaires(id, request));
    }

    @GetMapping("/bulletins/{id}/pdf")
    public ResponseEntity<byte[]> downloadBulletinPdf(@PathVariable Long id) {
        BulletinPaieResponse bulletin = paieService.getBulletinResponseById(id);
        byte[] pdfBytes = pdfExportService.exportBulletinPdf(bulletin);

        String filename = "fiche_de_paie_" + (bulletin.getMatricule() != null ? bulletin.getMatricule().replace("/", "_") : id) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/periodes/{periodeId}/excel")
    public ResponseEntity<byte[]> downloadEtatPaieExcel(@PathVariable Long periodeId) {
        PeriodePaie periode = paieService.getPeriodeById(periodeId);
        List<EtatPaieDto> rows = paieService.getEtatPaie(periodeId);
        List<BulletinPaieResponse> bulletins = paieService.getBulletinsByPeriode(periodeId);

        byte[] excelBytes = excelExportService.exportEtatPaieExcel(periode, rows, bulletins);
        String filename = "ETAT_DE_PAIE_" + periode.getCode() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
