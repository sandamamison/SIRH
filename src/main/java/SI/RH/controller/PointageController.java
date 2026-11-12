package SI.RH.controller;

import SI.RH.entity.AnomaliePointage;
import SI.RH.entity.Pointage;
import SI.RH.entity.enums.DirectionPointage;
import SI.RH.entity.enums.OriginePointage;
import SI.RH.service.PointageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pointages")
public class PointageController {

    private final PointageService pointageService;

    public PointageController(PointageService pointageService) {
        this.pointageService = pointageService;
    }

    public record PointageRequest(
            Long employeId,
            DirectionPointage direction,
            OriginePointage origine
    ) {}

    @PostMapping
    public ResponseEntity<Pointage> pointer(@RequestBody PointageRequest req) {
        Pointage pt = pointageService.pointer(req.employeId(), req.direction(), req.origine());
        return ResponseEntity.status(HttpStatus.CREATED).body(pt);
    }

    @GetMapping("/employe/{employeId}")
    public List<Pointage> getPointagesJournee(
            @PathVariable Long employeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate d = date != null ? date : LocalDate.now();
        return pointageService.getPointagesJournee(employeId, d);
    }

    @GetMapping("/employe/{employeId}/temps-travaille")
    public ResponseEntity<Long> getTempsTravailleMinutes(
            @PathVariable Long employeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate d = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(pointageService.calculerTempsTravailleMinutes(employeId, d));
    }

    @GetMapping("/employe/{employeId}/anomalies")
    public List<AnomaliePointage> getAnomalies(@PathVariable Long employeId) {
        return pointageService.getAnomalies(employeId);
    }
}
