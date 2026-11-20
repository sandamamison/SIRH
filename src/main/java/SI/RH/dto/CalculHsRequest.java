package SI.RH.dto;

import java.math.BigDecimal;
import java.util.List;

public class CalculHsRequest {
    private List<SemaineHsDto> semaines;

    public CalculHsRequest() {}

    public List<SemaineHsDto> getSemaines() { return semaines; }
    public void setSemaines(List<SemaineHsDto> semaines) { this.semaines = semaines; }

    public static class SemaineHsDto {
        private Integer numeroSemaine;
        private BigDecimal heures30;
        private BigDecimal heures50;
        private BigDecimal heuresWeekend;
        private BigDecimal heuresFerie;

        public SemaineHsDto() {}

        public Integer getNumeroSemaine() { return numeroSemaine; }
        public void setNumeroSemaine(Integer numeroSemaine) { this.numeroSemaine = numeroSemaine; }
        public BigDecimal getHeures30() { return heures30; }
        public void setHeures30(BigDecimal heures30) { this.heures30 = heures30; }
        public BigDecimal getHeures50() { return heures50; }
        public void setHeures50(BigDecimal heures50) { this.heures50 = heures50; }
        public BigDecimal getHeuresWeekend() { return heuresWeekend; }
        public void setHeuresWeekend(BigDecimal heuresWeekend) { this.heuresWeekend = heuresWeekend; }
        public BigDecimal getHeuresFerie() { return heuresFerie; }
        public void setHeuresFerie(BigDecimal heuresFerie) { this.heuresFerie = heuresFerie; }
    }
}
