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
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        /* ================================ */
                        /* 🔐 ENDPOINTS PÚBLICOS DE AUTH     */
                        /* ================================ */
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").permitAll()

                        /* ================================ */
                        /* 🧍 REGISTRO DE USUARIOS/EMPRESAS */
                        /* ================================ */
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/companies").permitAll()

                        /* ================================ */
                        /* 📂 ARCHIVOS PÚBLICOS (IMÁGENES)   */
                        /* ================================ */
                        .requestMatchers("/files/**").permitAll()

                        /* ================================ */
                        /* 🟦 JOBS PÚBLICOS                  */
                        /* ================================ */
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()

                        /* ================================ */
                        /* 📄 SUBIDA DE ARCHIVOS            */
                        /* ================================ */

                        // CV → candidatos
                        .requestMatchers(HttpMethod.POST, "/api/files/upload/cv/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // LOGO → empresas
                        .requestMatchers(HttpMethod.POST, "/api/files/upload/logo/**")
                        .hasAuthority("ROLE_COMPANY")

                        /* ================================ */
                        /* 🟧 POSTULACIONES (solo user)     */
                        /* ================================ */
                        .requestMatchers("/api/applications/**")
                        .hasAuthority("ROLE_USER")

                        /* ================================ */
                        /* 🏢 CRUD OFERTAS (empresa)        */
                        /* ================================ */
                        .requestMatchers(HttpMethod.POST, "/api/jobs/**").hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.PUT, "/api/jobs/**").hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasAuthority("ROLE_COMPANY")

                        /* ================================ */
                        /* 👤 USUARIOS Y EMPRESAS PRIVADO   */
                        /* ================================ */
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/companies/**").authenticated()

                        /* ================================ */
                        /* 🔐 RESTO → TOKEN OBLIGATORIO     */
                        /* ================================ */
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
