package SI.RH.repository;

import SI.RH.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByUsernameAndActifTrue(String username);

    @org.springframework.data.jpa.repository.Query(
        value = "select role from utilisateur_roles where utilisateurid = :id",
        nativeQuery = true
    )
    Optional<String> findRoleByUtilisateurId(@org.springframework.data.repository.query.Param("id") Long id);
}
