package SI.RH.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(name = "passwordhash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, unique = true)
    private Long personneid;

    @Column(nullable = false)
    private boolean actif = true;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Long getPersonneid() { return personneid; }
    public boolean isActif() { return actif; }
}
