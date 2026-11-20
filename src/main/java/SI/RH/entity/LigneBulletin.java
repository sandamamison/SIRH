package SI.RH.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ligne_bulletin")
public class LigneBulletin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bulletin_paie_id", nullable = false)
    private Long bulletinPaieId;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private String designation;

    @Column(precision = 10, scale = 2)
    private BigDecimal nombre;

    @Column(precision = 15, scale = 2)
    private BigDecimal taux;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, length = 20)
    private String type; // GAIN, RETENUE, COTISATION, IRSA, INFO

    @Column(nullable = false)
    private Integer ordre;

    public LigneBulletin() {}

    public LigneBulletin(Long bulletinPaieId, String code, String designation, BigDecimal nombre, BigDecimal taux, BigDecimal montant, String type, Integer ordre) {
        this.bulletinPaieId = bulletinPaieId;
        this.code = code;
        this.designation = designation;
        this.nombre = nombre;
        this.taux = taux;
        this.montant = montant;
        this.type = type;
        this.ordre = ordre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBulletinPaieId() { return bulletinPaieId; }
    public void setBulletinPaieId(Long bulletinPaieId) { this.bulletinPaieId = bulletinPaieId; }

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
