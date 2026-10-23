package SI.RH.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login.html", "/login", "/styles.css").permitAll()
                .requestMatchers("/", "/index.html", "/app.js").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/employes/**", "/api/departements/**", "/api/postes/**").hasAnyRole("CANDIDAT", "SALARIE", "MANAGER", "RH", "DIRECTION", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/demandes/**").hasAnyRole("MANAGER", "RH", "DIRECTION", "ADMIN")
                .requestMatchers("/api/demandes/*/valider", "/api/demandes/*/refuser").hasAnyRole("RH", "DIRECTION", "ADMIN")
                .requestMatchers("/api/demandes/*/soumettre").hasAnyRole("MANAGER", "RH", "DIRECTION", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/demandes/**").hasAnyRole("MANAGER", "RH", "DIRECTION", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/demandes/**").hasAnyRole("MANAGER", "RH", "DIRECTION", "ADMIN")
                .requestMatchers("/api/propositions-embauche/*/accepter", "/api/propositions-embauche/*/refuser").hasRole("CANDIDAT")
                .requestMatchers("/api/propositions-embauche/*/expirer").hasAnyRole("RH", "DIRECTION", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/propositions-embauche").hasAnyRole("RH", "DIRECTION", "ADMIN")
                .requestMatchers("/api/propositions-embauche/**", "/api/candidatures/**", "/api/offres/**").hasAnyRole("CANDIDAT", "RH", "DIRECTION", "ADMIN")
                .requestMatchers("/api/contrats/**", "/api/affectations/**").hasAnyRole("RH", "DIRECTION", "ADMIN")
                .requestMatchers("/api/entretiens/**", "/api/evaluations/**", "/api/intervenants-entretien/**").hasAnyRole("SALARIE", "RH", "DIRECTION", "ADMIN")
                .anyRequest().hasAnyRole("DIRECTION", "ADMIN")
            )
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login.html?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
