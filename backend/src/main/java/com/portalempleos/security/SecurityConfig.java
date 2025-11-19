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
                // CORS habilitado (usa tu CorsFilter personalizado)
                .cors(cors -> {})

                // CSRF desactivado para API REST
                .csrf(csrf -> csrf.disable())

                // Stateless: sin sesiones
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ====== AUTORIZACIÓN ======
                .authorizeHttpRequests(auth -> auth

                        // Permitir preflight OPTIONS SIEMPRE
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ====== RUTAS PÚBLICAS ======
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/companies/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/companies").permitAll()

                        .requestMatchers("/files/**").permitAll()

                        // ====== SUBIDA DE ARCHIVOS ======
                        .requestMatchers(HttpMethod.POST, "/api/files/upload/cv/**")
                                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/files/upload/logo/**")
                                .hasAuthority("ROLE_COMPANY")

                        // ====== POSTULACIONES ======
                        .requestMatchers(HttpMethod.POST, "/api/applications")
                                .hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/api/applications/user/**")
                                .hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/api/applications/job/**")
                                .hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.PATCH, "/api/applications/**")
                                .hasAnyAuthority("ROLE_COMPANY", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/applications/**")
                                .hasAnyAuthority("ROLE_USER", "ROLE_COMPANY", "ROLE_ADMIN")

                        // ====== CRUD EMPLEOS ======
                        .requestMatchers(HttpMethod.POST, "/api/jobs/**")
                                .hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.PUT, "/api/jobs/**")
                                .hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.DELETE, "/api/jobs/**")
                                .hasAuthority("ROLE_COMPANY")

                        // ====== CRUD USUARIOS ======
                        .requestMatchers("/api/users/**").authenticated()

                        // Empresas: solo PUT/DELETE protegidos
                        .requestMatchers(HttpMethod.PUT, "/api/companies/**")
                                .hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/**")
                                .hasAuthority("ROLE_ADMIN")

                        // ====== TODO LO DEMÁS PERMITIDO ======
                        .anyRequest().permitAll()
                );

        // Filtro JWT antes del de Username & Password
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
