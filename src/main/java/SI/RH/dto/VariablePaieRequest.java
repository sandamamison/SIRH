package SI.RH.dto;

import java.math.BigDecimal;

public class VariablePaieRequest {
    private Long employeId;
    private String type;
    private String libelle;
    private BigDecimal nombre;
    private BigDecimal taux;
    private BigDecimal montant;

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public BigDecimal getNombre() { return nombre; }
    public void setNombre(BigDecimal nombre) { this.nombre = nombre; }
    public BigDecimal getTaux() { return taux; }
    public void setTaux(BigDecimal taux) { this.taux = taux; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
}
