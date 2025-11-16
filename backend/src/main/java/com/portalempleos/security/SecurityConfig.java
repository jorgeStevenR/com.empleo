package com.portalempleos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {
                })
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        /* Rutas públicas */
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/companies").permitAll()
                        .requestMatchers("/files/**").permitAll()

                        /* Rutas protegidas específicas (van ANTES de /api/jobs/**) */
                        .requestMatchers(HttpMethod.GET, "/api/jobs/company/*").hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.GET, "/api/jobs/*/applications")
                            .hasAnyAuthority("ROLE_COMPANY", "ROLE_ADMIN")

                        /* Listado de empleos público */
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()

                        /* Gestión de empleos (solo empresas) */
                        .requestMatchers(HttpMethod.POST, "/api/jobs/**").hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.PUT, "/api/jobs/**").hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasAuthority("ROLE_COMPANY")

                        /* Postulaciones (candidatos) */
                        .requestMatchers(HttpMethod.POST, "/api/applications/**").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.PUT, "/api/applications/**").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/applications/**").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/api/applications/user/**").hasAuthority("ROLE_USER")

                        /* Cambio de estado de postulaciones */
                        .requestMatchers(HttpMethod.PATCH, "/api/applications/*/status")
                            .hasAnyAuthority("ROLE_COMPANY", "ROLE_ADMIN", "ROLE_USER")

                        /* Subida de archivos (extra seguridad por rol) */
                        .requestMatchers(HttpMethod.POST, "/api/files/upload/cv/**").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.POST, "/api/files/upload/logo/**").hasAuthority("ROLE_COMPANY")

                        /* Endpoints extra de actualización por entidad */
                        .requestMatchers(HttpMethod.PUT, "/api/companies/*/logo").hasAuthority("ROLE_COMPANY")

                        /* Resto de endpoints de usuarios / empresas requieren autenticación */
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/companies/**").authenticated()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
