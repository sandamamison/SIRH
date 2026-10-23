package SI.RH.security;

import SI.RH.entity.Utilisateur;
import SI.RH.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UtilisateurRepository repository;

    public DatabaseUserDetailsService(UtilisateurRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur user = repository.findByUsernameAndActifTrue(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu"));
        return User.withUsername(user.getUsername())
            .password(user.getPasswordHash())
            .roles(repository.findRoleByUtilisateurId(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Role absent")))
            .build();
    }
}
