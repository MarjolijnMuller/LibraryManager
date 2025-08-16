package org.example.librarymanager.security;

import org.example.librarymanager.services.InvoiceService;
import org.example.librarymanager.services.LoanService;
import org.example.librarymanager.services.ProfileService;
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
    private final ProfileService profileService;
    private final LoanService loanService;
    private final PasswordEncoder passwordEncoder;
    private final InvoiceService invoiceService;

    public SecurityConfig(JwtService jwtService, UserDetailsService userDetailsService, ProfileService profileService, LoanService loanService, PasswordEncoder passwordEncoder, InvoiceService invoiceService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.profileService = profileService;
        this.loanService = loanService;
        this.passwordEncoder = passwordEncoder;
        this.invoiceService = invoiceService;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
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
    public AuthorizationManager<RequestAuthorizationContext> loanOwnerAuthorizationManager() {
        return (authenticationSupplier, context) -> {
            var authentication = authenticationSupplier.get();
            boolean hasPrivilegedRole = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN") || grantedAuthority.getAuthority().equals("ROLE_LIBRARIAN"));
            if (hasPrivilegedRole) {
                return new AuthorizationDecision(true);
            }

            String authenticatedUsername = authentication.getName();
            String pathLoanIdString = context.getVariables().get("loanId");

            if (pathLoanIdString != null) {
                try {
                    Long pathLoanId = Long.parseLong(pathLoanIdString);
                    Long authenticatedUserId = profileService.getUserIdByUsername(authenticatedUsername);
                    return new AuthorizationDecision(loanService.isLoanOwner(pathLoanId, authenticatedUserId));
                } catch (NumberFormatException e) {
                    return new AuthorizationDecision(false);
                }
            }
            return new AuthorizationDecision(false);
        };
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> invoiceOwnerAuthorizationManager() {
        return (authenticationSupplier, context) -> {
            var authentication = authenticationSupplier.get();
            boolean hasPrivilegedRole = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN") || grantedAuthority.getAuthority().equals("ROLE_LIBRARIAN"));
            if (hasPrivilegedRole) {
                return new AuthorizationDecision(true);
            }

            String authenticatedUsername = authentication.getName();
            String pathInvoiceIdString = context.getVariables().get("id");

            if (pathInvoiceIdString != null) {
                try {
                    Long pathInvoiceId = Long.parseLong(pathInvoiceIdString);
                    return new AuthorizationDecision(invoiceService.isInvoiceOwner(pathInvoiceId, authenticatedUsername));
                } catch (NumberFormatException e) {
                    return new AuthorizationDecision(false);
                }
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
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()

                        .requestMatchers(HttpMethod.GET, "/books", "/books/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/books", "/books/copies").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/books/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/books/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/books/**").hasAnyRole("LIBRARIAN", "ADMIN")

                        .requestMatchers("/fines", "/fines/**").hasAnyRole("LIBRARIAN", "ADMIN")

                        .requestMatchers(HttpMethod.GET,"/invoices/user").authenticated()
                        .requestMatchers(HttpMethod.GET, "/invoices/{id}").access(invoiceOwnerAuthorizationManager())
                        .requestMatchers(HttpMethod.GET, "/invoices/{id}/pdf").access(invoiceOwnerAuthorizationManager())
                        .requestMatchers("/invoices", "/invoices/**").hasAnyRole("LIBRARIAN", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/profiles").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/profiles/members", "/profiles/members/**", "/profiles/librarians", "/profiles/librarians/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/profiles").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/profiles/{userId}", "/profiles/username/{username}").access(profileOwnerAuthorizationManager())
                        .requestMatchers(HttpMethod.PUT, "/profiles/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/profiles/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/profiles/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/loans/user").authenticated()
                        .requestMatchers(HttpMethod.GET, "/loans/user/{userId}").access(profileOwnerAuthorizationManager())
                        .requestMatchers(HttpMethod.PATCH, "/loans/{loanId}/return").access(loanOwnerAuthorizationManager())
                        .requestMatchers(HttpMethod.GET, "/loans/{loanId}/receipt").access(loanOwnerAuthorizationManager())
                        .requestMatchers(HttpMethod.POST, "/loans").authenticated()
                        .requestMatchers("/loans", "/loans/**").hasAnyRole("LIBRARIAN", "ADMIN")

                        .anyRequest().denyAll()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}