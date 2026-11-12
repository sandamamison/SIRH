package SI.RH.entity;

import SI.RH.entity.enums.DirectionPointage;
import SI.RH.entity.enums.OriginePointage;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pointage")
public class Pointage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DirectionPointage direction;

    @Column(nullable = false)
    private LocalDateTime horodatage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OriginePointage origine;

    private Long planningId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Pointage() {}

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public DirectionPointage getDirection() {
        return direction;
    }

    public void setDirection(DirectionPointage direction) {
        this.direction = direction;
    }

    public LocalDateTime getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(LocalDateTime horodatage) {
        this.horodatage = horodatage;
    }

    public OriginePointage getOrigine() {
        return origine;
    }

    public void setOrigine(OriginePointage origine) {
        this.origine = origine;
    }

    public Long getPlanningId() {
        return planningId;
    }

    public void setPlanningId(Long planningId) {
        this.planningId = planningId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
