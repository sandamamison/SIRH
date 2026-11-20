package SI.RH.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BulletinPaieResponse {
    private Long id;
    private Long periodePaieId;
    private String periodeCode;
    private Long employeId;
    private String matricule;
    private String nom;
    private String prenom;
    private String fonction;
    private String numeroCnaps;
    private LocalDate dateEmbauche;
    private String ancienneteTexte;
    private String classification;
    private BigDecimal salaireBase;
    private BigDecimal tauxJournalier;
    private BigDecimal tauxHoraire;
    private BigDecimal indice;
    private BigDecimal totalHs;
    private BigDecimal totalAbsenceDeductible;
    private BigDecimal totalPrimes;
    private BigDecimal salaireBrut;
    private BigDecimal retenueCnaps;
    private BigDecimal retenueSanitaire;
    private BigDecimal totalIrsa;
    private BigDecimal totalRetenues;
    private BigDecimal autresIndemnites;
    private BigDecimal netAPayer;
    private String statut;
    private List<LigneBulletinDto> lignes;
    private List<CalculHsDto> calculHsSemaines;

    public BulletinPaieResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPeriodePaieId() { return periodePaieId; }
    public void setPeriodePaieId(Long periodePaieId) { this.periodePaieId = periodePaieId; }
    public String getPeriodeCode() { return periodeCode; }
    public void setPeriodeCode(String periodeCode) { this.periodeCode = periodeCode; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getFonction() { return fonction; }
    public void setFonction(String fonction) { this.fonction = fonction; }
    public String getNumeroCnaps() { return numeroCnaps; }
    public void setNumeroCnaps(String numeroCnaps) { this.numeroCnaps = numeroCnaps; }
    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }
    public String getAncienneteTexte() { return ancienneteTexte; }
    public void setAncienneteTexte(String ancienneteTexte) { this.ancienneteTexte = ancienneteTexte; }
    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }
    public BigDecimal getSalaireBase() { return salaireBase; }
    public void setSalaireBase(BigDecimal salaireBase) { this.salaireBase = salaireBase; }
    public BigDecimal getTauxJournalier() { return tauxJournalier; }
    public void setTauxJournalier(BigDecimal tauxJournalier) { this.tauxJournalier = tauxJournalier; }
    public BigDecimal getTauxHoraire() { return tauxHoraire; }
    public void setTauxHoraire(BigDecimal tauxHoraire) { this.tauxHoraire = tauxHoraire; }
    public BigDecimal getIndice() { return indice; }
    public void setIndice(BigDecimal indice) { this.indice = indice; }
    public BigDecimal getTotalHs() { return totalHs; }
    public void setTotalHs(BigDecimal totalHs) { this.totalHs = totalHs; }
    public BigDecimal getTotalAbsenceDeductible() { return totalAbsenceDeductible; }
    public void setTotalAbsenceDeductible(BigDecimal totalAbsenceDeductible) { this.totalAbsenceDeductible = totalAbsenceDeductible; }
    public BigDecimal getTotalPrimes() { return totalPrimes; }
    public void setTotalPrimes(BigDecimal totalPrimes) { this.totalPrimes = totalPrimes; }
    public BigDecimal getSalaireBrut() { return salaireBrut; }
    public void setSalaireBrut(BigDecimal salaireBrut) { this.salaireBrut = salaireBrut; }
    public BigDecimal getRetenueCnaps() { return retenueCnaps; }
    public void setRetenueCnaps(BigDecimal retenueCnaps) { this.retenueCnaps = retenueCnaps; }
    public BigDecimal getRetenueSanitaire() { return retenueSanitaire; }
    public void setRetenueSanitaire(BigDecimal retenueSanitaire) { this.retenueSanitaire = retenueSanitaire; }
    public BigDecimal getTotalIrsa() { return totalIrsa; }
    public void setTotalIrsa(BigDecimal totalIrsa) { this.totalIrsa = totalIrsa; }
    public BigDecimal getTotalRetenues() { return totalRetenues; }
    public void setTotalRetenues(BigDecimal totalRetenues) { this.totalRetenues = totalRetenues; }
    public BigDecimal getAutresIndemnites() { return autresIndemnites; }
    public void setAutresIndemnites(BigDecimal autresIndemnites) { this.autresIndemnites = autresIndemnites; }
    public BigDecimal getNetAPayer() { return netAPayer; }
    public void setNetAPayer(BigDecimal netAPayer) { this.netAPayer = netAPayer; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public List<LigneBulletinDto> getLignes() { return lignes; }
    public void setLignes(List<LigneBulletinDto> lignes) { this.lignes = lignes; }
    public List<CalculHsDto> getCalculHsSemaines() { return calculHsSemaines; }
    public void setCalculHsSemaines(List<CalculHsDto> calculHsSemaines) { this.calculHsSemaines = calculHsSemaines; }

    public static class LigneBulletinDto {
        private String code;
        private String designation;
        private BigDecimal nombre;
        private BigDecimal taux;
        private BigDecimal montant;
        private String type;
        private Integer ordre;

        public LigneBulletinDto() {}
        public LigneBulletinDto(String code, String designation, BigDecimal nombre, BigDecimal taux, BigDecimal montant, String type, Integer ordre) {
            this.code = code;
            this.designation = designation;
            this.nombre = nombre;
            this.taux = taux;
            this.montant = montant;
            this.type = type;
            this.ordre = ordre;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }
        public BigDecimal getNombre() { return nombre; }
        public void setNombre(BigDecimal nombre) { this.nombre = nombre; }
        public BigDecimal getTaux() { return taux; }
        public void setTaux(BigDecimal taux) { this.taux = taux; }
        public BigDecimal getMontant() { return montant; }
        public void setMontant(BigDecimal montant) { this.montant = montant; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getOrdre() { return ordre; }
        public void setOrdre(Integer ordre) { this.ordre = ordre; }
    }

    public static class CalculHsDto {
        private Integer numeroSemaine;
        private BigDecimal heures30;
        private BigDecimal heures50;
        private BigDecimal heuresWeekend;
        private BigDecimal heuresFerie;
        private BigDecimal montant30;
        private BigDecimal montant50;
        private BigDecimal montantWeekend;
        private BigDecimal montantFerie;
        private BigDecimal totalHeures;
        private BigDecimal montantSemaine;

        public CalculHsDto() {}

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
        public BigDecimal getMontant30() { return montant30; }
        public void setMontant30(BigDecimal montant30) { this.montant30 = montant30; }
        public BigDecimal getMontant50() { return montant50; }
        public void setMontant50(BigDecimal montant50) { this.montant50 = montant50; }
        public BigDecimal getMontantWeekend() { return montantWeekend; }
        public void setMontantWeekend(BigDecimal montantWeekend) { this.montantWeekend = montantWeekend; }
        public BigDecimal getMontantFerie() { return montantFerie; }
        public void setMontantFerie(BigDecimal montantFerie) { this.montantFerie = montantFerie; }
        public BigDecimal getTotalHeures() { return totalHeures; }
        public void setTotalHeures(BigDecimal totalHeures) { this.totalHeures = totalHeures; }
        public BigDecimal getMontantSemaine() { return montantSemaine; }
        public void setMontantSemaine(BigDecimal montantSemaine) { this.montantSemaine = montantSemaine; }
    }
}
