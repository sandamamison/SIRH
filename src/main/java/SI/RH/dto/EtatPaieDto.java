package SI.RH.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EtatPaieDto {
    private LocalDate date;
    private String nbre;
    private String matricule;
    private String numeroCnaps;
    private String nomPrenoms;
    private LocalDate dateEmbauche;
    private BigDecimal absenceMois;
    private String categorie;
    private String fonction;
    private BigDecimal salaireBase;
    private BigDecimal retenues;
    private BigDecimal rappelAutres;
    private BigDecimal heuresSupplementaires;
    private BigDecimal totalBrut;
    private BigDecimal totalIrsa;
    private BigDecimal netAPayer;
    private Long bulletinId;

    public EtatPaieDto() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getNbre() { return nbre; }
    public void setNbre(String nbre) { this.nbre = nbre; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getNumeroCnaps() { return numeroCnaps; }
    public void setNumeroCnaps(String numeroCnaps) { this.numeroCnaps = numeroCnaps; }
    public String getNomPrenoms() { return nomPrenoms; }
    public void setNomPrenoms(String nomPrenoms) { this.nomPrenoms = nomPrenoms; }
    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }
    public BigDecimal getAbsenceMois() { return absenceMois; }
    public void setAbsenceMois(BigDecimal absenceMois) { this.absenceMois = absenceMois; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getFonction() { return fonction; }
    public void setFonction(String fonction) { this.fonction = fonction; }
    public BigDecimal getSalaireBase() { return salaireBase; }
    public void setSalaireBase(BigDecimal salaireBase) { this.salaireBase = salaireBase; }
    public BigDecimal getRetenues() { return retenues; }
    public void setRetenues(BigDecimal retenues) { this.retenues = retenues; }
    public BigDecimal getRappelAutres() { return rappelAutres; }
    public void setRappelAutres(BigDecimal rappelAutres) { this.rappelAutres = rappelAutres; }
    public BigDecimal getHeuresSupplementaires() { return heuresSupplementaires; }
    public void setHeuresSupplementaires(BigDecimal heuresSupplementaires) { this.heuresSupplementaires = heuresSupplementaires; }
    public BigDecimal getTotalBrut() { return totalBrut; }
    public void setTotalBrut(BigDecimal totalBrut) { this.totalBrut = totalBrut; }
    public BigDecimal getTotalIrsa() { return totalIrsa; }
    public void setTotalIrsa(BigDecimal totalIrsa) { this.totalIrsa = totalIrsa; }
    public BigDecimal getNetAPayer() { return netAPayer; }
    public void setNetAPayer(BigDecimal netAPayer) { this.netAPayer = netAPayer; }
    public Long getBulletinId() { return bulletinId; }
    public void setBulletinId(Long bulletinId) { this.bulletinId = bulletinId; }
}
