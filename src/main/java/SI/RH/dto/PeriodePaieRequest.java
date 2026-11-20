package SI.RH.dto;

import java.time.LocalDate;

public class PeriodePaieRequest {
    private Integer mois;
    private Integer annee;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public PeriodePaieRequest() {}

    public Integer getMois() { return mois; }
    public void setMois(Integer mois) { this.mois = mois; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}
