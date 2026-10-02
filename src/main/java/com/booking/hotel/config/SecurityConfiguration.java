package com.booking.hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            SecurityContextRepository securityContextRepository
    ) throws Exception {
        HttpSessionCsrfTokenRepository csrfRepository = new HttpSessionCsrfTokenRepository();

        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfRepository)
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository)
                        .requireExplicitSave(true))
                .authorizeHttpRequests(registry -> {
                    registry.dispatcherTypeMatchers(FORWARD, ERROR).permitAll();
                    registry.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    registry.requestMatchers("/actuator/health", "/actuator/health/**").permitAll();
                    registry.requestMatchers("/api/v1/auth/login", "/api/v1/auth/csrf").permitAll();
                    registry.requestMatchers("/api/v1/auth/me", "/api/v1/auth/logout").authenticated();
                    registry.requestMatchers(
                            "/api/v1/services/**",
                            "/api/v1/clients/**",
                            "/api/v1/contracts/**",
                            "/api/v1/bookings/**",
                            "/api/v1/accounts/**",
                            "/api/v1/staff/**"
                    ).hasAnyRole("MANAGER", "ADMIN");
                    registry.anyRequest().denyAll();
                })
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((request, response, exception) -> {
                            response.setStatus(403);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\"}");
                        }))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.frontend.origin:http://localhost:5173}") String frontendOrigin
    ) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "X-CSRF-TOKEN"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails normalUser = User.builder()
                .username("manager")
                .password("$2a$12$xOIYYujxAOmczE0MoEjayehbthwwqDKlWGPS9o2BKGZmiKG9cJVg2")
                .roles("MANAGER")
                .build();
        UserDetails adminUser = User.builder()
                .username("admin")
                .password("$2a$12$5QM7j4KXIXId6Nm/81zseOxTIfGFuNHEyRs.tH.RU.VNE11fefEqm")
                .roles("ADMIN")
                .build();

        UserDetails hotelManagementServiceUser = User.builder()
                .username("maids")
                .password("$2a$12$wKrAloN8sKDsM9QdXlH0O.7nUvIC4iQWCMOlgjHPygc26xLBPzLIa")
                .roles("HOTEL_MANAGEMENT_SERVICE")
                .build();
        return new InMemoryUserDetailsManager(normalUser, adminUser, hotelManagementServiceUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
