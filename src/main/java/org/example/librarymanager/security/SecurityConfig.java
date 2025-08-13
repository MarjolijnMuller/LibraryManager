package org.example.librarymanager.security;

import org.example.librarymanager.services.ProfileService; // Voeg deze import toe
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ProfileService profileService; // Injecteer de ProfileService

    public SecurityConfig(JwtService jwtService, UserDetailsService userDetailsService, ProfileService profileService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.profileService = profileService;
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        var auth = new DaoAuthenticationProvider();
        auth.setPasswordEncoder(passwordEncoder);
        auth.setUserDetailsService(userDetailsService);
        return new ProviderManager(auth);
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtService, userDetailsService);
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> profileOwnerAuthorizationManager() {
        return (authenticationSupplier, context) -> {
            var authentication = authenticationSupplier.get();
            boolean hasPrivilegedRole = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN") || grantedAuthority.getAuthority().equals("ROLE_LIBRARIAN"));

            if (hasPrivilegedRole) {
                return new AuthorizationDecision(true);
            }

            String authenticatedUsername = authentication.getName();
            String pathUserIdString = context.getVariables().get("userId");

            if (pathUserIdString != null) {
                try {
                    Long pathUserId = Long.parseLong(pathUserIdString);
                    Long authenticatedUserId = profileService.getUserIdByUsername(authenticatedUsername);
                    return new AuthorizationDecision(pathUserId.equals(authenticatedUserId));
                } catch (NumberFormatException e) {
                    return new AuthorizationDecision(false);
                }
            }

            String requestedUsername = context.getVariables().get("username");
            if (requestedUsername != null) {
                return new AuthorizationDecision(authenticatedUsername.equalsIgnoreCase(requestedUsername));
            }

            return new AuthorizationDecision(false);
        };
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                        .requestMatchers(HttpMethod.GET, "/books", "/books/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/books", "/books/copies").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/books/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/books/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/books/**").hasAnyRole("LIBRARIAN", "ADMIN")

                        .requestMatchers("/fines", "/fines/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers("/invoices", "/invoices/**").hasAnyRole("LIBRARIAN", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/profiles").hasAnyRole("LIBRARIAN", "ADMIN")

                        // Herschikte regels: De meer specifieke regels staan nu bovenaan
                        .requestMatchers(HttpMethod.GET, "/profiles/members", "/profiles/members/**", "/profiles/librarians", "/profiles/librarians/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/profiles").hasAnyRole("LIBRARIAN", "ADMIN")

                        // Gebruik de custom autorisatiemanager voor profieltoegang
                        .requestMatchers(HttpMethod.GET, "/profiles/{userId}", "/profiles/username/{username}").access(profileOwnerAuthorizationManager())

                        .requestMatchers(HttpMethod.PUT, "/profiles/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/profiles/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/profiles/**").hasRole("ADMIN")

                        .requestMatchers("/loans", "/loans/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers("/books/copies").hasAnyRole("LIBRARIAN", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/loans").authenticated()
                        .requestMatchers(HttpMethod.GET, "/loans/user/{userId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/invoices/{invoiceId}").authenticated()

                        .anyRequest().denyAll()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}